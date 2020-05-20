package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fudan.se.lab2.repository.RoleRepository;
import fudan.se.lab2.repository.UserRoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;


@Entity                     //实体类的注解，必须注明
@Table(name = "user")      //指定对应的数据库表
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})

public class User implements UserDetails {

    private static final long serialVersionUID = -6140085056226164016L;

    @Id     //使用Id注解，表明是主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) //主键生成策略：自增型
    @Column(name = "id")    //对应数据库中的字段名称
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "real_name")
    private String realname;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "region")
    private String region;

    @Column(name = "organization")
    private String organization;

//
//    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//    @JoinTable(name = "user_authority",joinColumns = @JoinColumn(name="user_id"),inverseJoinColumns=@JoinColumn(name="authority_id"))
//    private Set<Authority> authorities = new HashSet<>();
//

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    /**
     * user_role  一对多
     *  用户只和角色有直接对应，只通过角色添加权限
     */
    @OneToMany(mappedBy ="user",cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<UserRole> userRoles = new HashSet<>();

    public User(){}

    public User(String username, String realname) {
        this.username = username;
        this.realname = realname;
    }

    /**
     * @apiNote default role: ROLE_user
     */
    public User(String username, String realname, String password, String email, String region, String organization){
        this.username = username;
        this.realname = realname;
        this.password = password;
        this.email = email;
        this.region = region;
        this.organization = organization;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        Set<GrantedAuthority> authoritySet = new HashSet<>();   //去重
        for (UserRole role: userRoles) {
            authoritySet.add(new SimpleGrantedAuthority(role.getRole().getRoleName()));
        }
//        //将role、authority名加入权限表
//        for (UserRole role: userRoles) {
//            Set<Authority> roleAuthorities = role.getRole().getRoleAuthority();
//            for(Authority roleAuthority : roleAuthorities)
//                authoritySet.add(new SimpleGrantedAuthority(roleAuthority.getAuthName()));
//            authoritySet.add(new SimpleGrantedAuthority(role.getRole().getRoleName()));
//        }
        authorities.addAll(authoritySet);
        return authorities;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public Long getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
