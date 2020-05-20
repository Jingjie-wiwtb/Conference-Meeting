package fudan.se.lab2.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@RestController
@Table(name = "authority")      //指定对应的数据库表
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
@Validated//使对@ReuquestParam的@Min注解生效
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/register", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> register(@Validated @RequestBody RegisterRequest request, BindingResult bindingResult) throws JSONException {
//        logger.debug("RegistrationForm: " + request.toString() );
//test
        JSONObject result = Tool.DealParamError(bindingResult);
        if (result != null){
            return new ResponseEntity<>(result.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        String message = authService.register(request);
        return Tool.getResponseEntity(message);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/login", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest request, BindingResult bindingResult) throws JSONException {
//        System.out.println("registerrequsr received!");
//        logger.debug("LoginForm: " + request.toString());
        JSONObject result = Tool.DealParamError(bindingResult);
        if (result != null){
            return new ResponseEntity<>(result.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        result = new JSONObject();
        String message = authService.login(request.getUsername(), request.getPassword());
//        System.out.println("message: " + message);
        if(message.contains("success")){
            //----------test------------
            String token = message.substring(7);
//            System.out.println("login success, token = " + token);
            //----------------------------
            result.put("message", "success");
            result.put("token",token);
//            result.put("userDetails",userDetailsService.loadUserByUsername(username));
            //用jsonobject.toStirng()方式传输
            return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK); //200
        }
        else {   //wrong password/use not found
            return Tool.getResponseEntity(message);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/find_user",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> find_user(@Validated @RequestParam(value = "realname") @NotBlank String realname) throws JSONException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        JSONObject result = authService.find_user(username, realname);
        if (result.isEmpty()){
            result.put("message", "No such user");
            return new ResponseEntity<>(result.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        result.put("message", "success");
        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }

    @GetMapping(value = "/my_information", produces = "application/json")
    public ResponseEntity<?> my_information() {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        JSONObject result = authService.my_information(username);
        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }

    /**
     * This is a function to test your connectivity. (健康测试时，可能会用到它）.
     */
    @GetMapping(value = "/welcome", produces = "application/json")
    public ResponseEntity<?> welcome() {
        Map<String, String> response = new HashMap<>();
        String message = "Welcome to 2020 Software Engineering Lab2. ";
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

}



