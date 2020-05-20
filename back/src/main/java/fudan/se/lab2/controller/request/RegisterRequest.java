package fudan.se.lab2.controller.request;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class RegisterRequest {

    @NotBlank(message = "用户名不能为null,长度必须大于0")
    private String username;

    @NotNull(message = "密码不能为null")
    @Length(min = 5, max = 12, message = "密码长度必须在5位到12位之间")
    private String password;

    @NotBlank(message = "真实姓名不能为null,长度必须大于0")
    private String realname;

    /*
    **
    *
     */
    @Email(regexp = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$", message = "邮箱格式错误")
    @NotBlank(message = "邮箱格式错误")
    private String email;

    @NotBlank(message = "区域不能为null,长度必须大于0")
    private String region;

    @NotBlank(message = "单位不能为null,长度必须大于0")
    private String organization;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, String realname, String email, String region, String organization) {
        this.username = username;
        this.password = password;
        this.realname = realname;
        this.email = email;
        this.region = region;
        this.organization = organization;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
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

