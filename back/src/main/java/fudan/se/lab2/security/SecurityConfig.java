package fudan.se.lab2.security;

import fudan.se.lab2.security.jwt.JwtConfigProperties;
import fudan.se.lab2.security.jwt.JwtRequestFilter;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import fudan.se.lab2.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private JwtUserDetailsService userDetailsService;
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(JwtUserDetailsService userDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //  Configure your auth here. Remember to read the JavaDoc carefully.
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // you need to configure your http security. Remember to read the JavaDoc carefully.
        //因为使用了JWT，故不需要HttpSession
        // We dont't need CSRF for this project.
        http.csrf().disable()
                // Make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //测试接口放行
                .antMatchers("/welcome").permitAll()
                //登录、注册接口放行
                .antMatchers("/login").permitAll()
                .antMatchers("/register").permitAll()
                //会议审核权限
                .antMatchers("/audit").hasRole("admin")
                //邀请会议的PC member权限
//                .antMatchers("/invite_pc").hasAuthority("AUTH_invite_pc")
//                //接受邀请并成为PC member权限
//                .antMatchers("/accept_invitation").hasAuthority("AUTH_accept_invitation")
//                //开启投稿权限
//                .antMatchers("/start_submission").hasAuthority("AUTH_start_submission")
//                //论文投稿权限
//                .antMatchers("/submit_paper").hasAuthority("AUTH_submit_paper")
//                //我的会议（我是chair）
//                .antMatchers("/myConferenceChair").hasAuthority("AUTH_myConferenceChair")
//                //会议详情
//                .antMatchers("/conference_detail").hasAuthority("AUTH_conference_detail")
//                //所有会议（投稿中）
//                .antMatchers("/submitting").hasAuthority("AUTH_submitting")
//                //所有会议（其他）
//                .antMatchers("/others").hasAuthority("AUTH_others")
//                //管理员获得所有未审核/已审核会议
//                .antMatchers("/get_conference").hasAuthority("AUTH_get_conference")
//                //我的会议（我不是chair）
//                .antMatchers("/myConferenceNotChair").hasAuthority("AUTH_myConferenceNotChair")
//                //邀请信息（消息通知）
//                .antMatchers("/invite_message").hasAuthority("AUTH_invite_message")
//                //查找用户
//                .antMatchers("/find_user").hasAuthority("AUTH_find_user")
                //邀请会议的PC member权限
                .antMatchers("/invite_pc").hasRole("user")
                //接受邀请并成为PC member权限
                .antMatchers("/accept_invitation").hasRole("user")
                //开启投稿权限
                .antMatchers("/start_submission").hasRole("user")
                //论文投稿权限
                .antMatchers("/submit_paper").hasRole("user")
                //我的会议（我是chair）
                .antMatchers("/myConferenceChair").hasRole("user")
                //会议详情
                .antMatchers("/conference_detail").hasRole("user")
                //所有会议（投稿中）
                .antMatchers("/submitting").hasRole("user")
                //所有会议（其他）
                .antMatchers("/others").hasRole("user")
                //管理员获得所有未审核/已审核会议
                .antMatchers("/get_conference").hasRole("admin")
                //我的会议（我不是chair）
                .antMatchers("/myConferenceNotChair").hasRole("user")
                //邀请信息（消息通知）
                .antMatchers("/invite_message").hasRole("user")
                //查找用户
                .antMatchers("/find_user").hasRole("user")
                //其他接口全部接受验证
                .anyRequest().authenticated();

//      Here we use JWT(Json Web Token) to authenticate the user.
//      You need to write your code in the class 'JwtRequestFilter' to make it works.
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers().cacheControl();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Hint: Now you can view h2-console page at `http://IP-Address:<port>/h2-console` without authentication.
        web.ignoring().antMatchers("/h2-console/**");
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
