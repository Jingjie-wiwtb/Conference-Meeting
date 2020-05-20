package fudan.se.lab2.controller.request;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LoginRequest {

    @NotBlank(message = "用户名不能为null,长度必须大于0")
    private String username;

    @NotNull(message = "密码不能为null")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String username, String password){
        this.username = username;
        this.password = password;
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
}
