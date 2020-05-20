package fudan.se.lab2.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Write your code to make this filter works.
 *
 * @author LBW
 */
@Component

public class JwtRequestFilter extends OncePerRequestFilter {

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //  Implement the filter.
        String token = request.getHeader("Authorization");
        //客户端请求若带上了token
        if (!StringUtils.isEmpty(token)){
            try {
                String username = jwtTokenUtil.getUsernameFromToken(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    //通过用户名获取用户信息
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    //验证jwt是否过期
                    if (jwtTokenUtil.validateToken(token, userDetails)) {
                    /*
                    加载用户、角色、权限信息，Spring Security根据这些信息判断接口的访问权限
                    用已知的用户名和密码创建一个UsernamePasswordAuthenticationToken()对象：
                    1、把用户名和密码都设置到自己本地变量上
                    2、super(userDetails.getAuthorities());父类构造函数需要传一组权限进来
                    3、setAuthenticated(true);表示前面存储进去的信息是否经过身份认证
                    */
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        //把请求的信息设置到UsernamePasswordAuthenticationToken里面，包括发请求的ip、session等
                        authentication.setDetails(new WebAuthenticationDetailsSource()
                                .buildDetails(request));
                        //存放authentication到SecurityContextHolder
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            catch (ExpiredJwtException e){
                returnResponse(response, "Token Expired");
            }
        }
        filterChain.doFilter(request, response);
    }

    private void returnResponse(HttpServletResponse response, String data) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json, text/plain, */*");
        response.setStatus(403);
        try {
            writer = response.getWriter();
            writer.print(data);
        }catch (IOException e){

        }
        finally {
            if (writer != null)
                writer.close();
        }
    }
}

/*

Authentication是一个接口，用来表示用户认证信息，在用户登录认证之前相关信息会封装为一个Authentication具体实现类的对象，
在登录认证成功之后又会生成一个信息更全面，包含用户权限等信息的Authentication对象，然后把它保存在SecurityContextHolder
所持有的SecurityContext中，供后续的程序进行调用，如访问权限的鉴定等。

SecurityContextHolder是用来保存SecurityContext的。SecurityContext中含有当前正在访问系统的用户的详细信息。默认情况下，
SecurityContextHolder将使用ThreadLocal来保存SecurityContext，这也就意味着在处于同一线程中的方法中我们可以从ThreadLocal中
获取到当前的SecurityContext。因为线程池的原因，如果我们每次在请求完成后都将ThreadLocal进行清除的话，那么我们把SecurityContext
存放在ThreadLocal中还是比较安全的。这些工作Spring Security已经自动为我们做了，即在每一次request结束后都将清除当前线程的ThreadLocal。

Spring Security使用一个Authentication对象来描述当前用户的相关信息。SecurityContextHolder中持有的是当前用户的SecurityContext，
而SecurityContext持有的是代表当前用户相关信息的Authentication的引用。这个Authentication对象不需要我们自己去创建，在与系统交互的过程中，
Spring Security会自动为我们创建相应的Authentication对象，然后赋值给当前的SecurityContext。但是往往我们需要在程序中获取当前用户的相关信息，
比如最常见的是获取当前登录用户的用户名。此外，调用SecurityContextHolder.getContext()获取SecurityContext时，如果对应的SecurityContext不
存在，则Spring Security将为我们建立一个空的SecurityContext并进行返回。

GrantedAuthority
Authentication的getAuthorities()可以返回当前Authentication对象拥有的权限，即当前用户拥有的权限。其返回值是一个GrantedAuthority类型的数组，
每一个GrantedAuthority对象代表赋予给当前用户的一种权限。GrantedAuthority是一个接口，其通常是通过UserDetailsService进行加载，然后赋予给
UserDetails的。GrantedAuthority中只定义了一个getAuthority()方法，该方法返回一个字符串，表示对应权限的字符串表示，如果对应权限不能用字符串
表示，则应当返回null。Spring Security针对GrantedAuthority有一个简单实现SimpleGrantedAuthority。该类只是简单的接收一个表示权限的字符串。
Spring Security内部的所有AuthenticationProvider都是使用SimpleGrantedAuthority来封装Authentication对象。

*/
