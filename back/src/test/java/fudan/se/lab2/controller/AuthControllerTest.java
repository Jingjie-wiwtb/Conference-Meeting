package fudan.se.lab2.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.security.jwt.JwtRequestFilter;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.service.JwtUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//系统你执行测试方法时，调用者是SpringRunnerClass类
@RunWith(SpringRunner.class)
//测试时如果涉及了数据库的操作，那么测试完成后，该操作会回滚，也就是不会改变数据库内容
@Rollback
@Transactional(rollbackOn = Exception.class)
@SpringBootTest
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @SpyBean
    private AuthService authService;

    /*
    **SecurityConfig需要JwtUserDetailsService的provider
    * 但是MockBean UserDetailsService可能把它屏蔽掉了
    * 所以这里只能@MockBean JwtUserDetailsService
    * 而不能@MockBean UserDetailsService
    * 一个bug五小时的血泪史（手动微笑
     */
    @SpyBean
    private JwtUserDetailsService userDetailsService;

    @SpyBean
    private JwtTokenUtil jwtTokenUtil;

    //将对象转化为JsonString输出
    private final static ObjectMapper objectMapper = new ObjectMapper();

    /*
    **MockMvc 是由spring-test包提供，实现了对Http请求的模拟，能够直接使用网络的形式，转换到Controller的调用，使得测试速度快、
    * 不依赖网络环境。同时提供了一套验证的工具，结果的验证十分方便。接口MockMvcBuilder，提供一个唯一的build方法，用来构造MockMvc。
    *
    * MockMvcBuilder 是用来构造MockMvc的构造器,其主要有两个实现 : StandaloneMockMvcBuilder和DefaultMockMvcBuilder，
    * 分别对应两种测试方式，即独立安装和集成Web环境测试（此种方式并不会集成真正的web环境，而是通过相应的Mock API进行模拟测
    * 试，无须启动服务器）。对于我们来说直接使用静态工厂MockMvcBuilders创建即可。
    * 此处采用独立安装方式
     */

    private MockMvc mockMvc;

    //@BeforeEach:初始化方法，对于每一个测试方法都要执行一次（注意与BeforeClass区别，后者是对于所有方法只执行一次）
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).addFilter(jwtRequestFilter).build();
    }

    @Test
    void register() throws Exception {

        doReturn("success").when(authService).register(any(RegisterRequest.class));

        //外部测试，也就是并不知道spring内部已经对request的成员非空进行了校验，故此处需要校验
        RegisterRequest request_1 = new RegisterRequest(null, "test_password", "test_realname", "123@123.com", "test_region", "test_orgnazition");
        RegisterRequest request_2 = new RegisterRequest("test_username", null, "test_realname", "123@123.com", "test_region", "test_orgnazition");
        RegisterRequest request_3 = new RegisterRequest("test_username", "test_pwd", null, "123@123.com", "test_region", "test_orgnazition");
        RegisterRequest request_4 = new RegisterRequest("test_username", "test_pwd", "test_realname", null, "test_region", "test_orgnazition");
        RegisterRequest request_5 = new RegisterRequest("test_username", "test_pwd", "test_realname", "123@123.com", null, "test_orgnazition");
        RegisterRequest request_6 = new RegisterRequest("test_username", "test_pwd", "test_realname", "123@123.com", "test_region", null);

        RegisterRequest request = new RegisterRequest("test_username", "test_pwd", "test_realname", "123@123.com", "test_region", "test_orgnazition");

//        JSONObject result = new JSONObject();
//        result.put("message", "parameter error");
        /*
        **perform：执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
        * get:声明发送一个get请求的方法。MockHttpServletRequestBuilder get(String urlTemplate, Object... urlVariables)：根据uri模板和uri变量值得到一个GET请求方式的。
        *     另外提供了其他的请求的方法，如：post、put、delete等。
        * andExpect：添加ResultMatcher验证规则，验证控制器执行完成后结果是否正确（对返回的数据进行的判断）；
        * andDo：添加ResultHandler结果处理器，比如调试时打印结果到控制台（对返回的数据进行的判断）；
        * andReturn：最后返回相应的MvcResult；然后进行自定义验证/进行下一步的异步处理（对返回的数据进行的判断）；
         */
        model_post_not_token("/register", request_1, "register", false);
        request_1.setUsername("");
        model_post_not_token("/register", request_1, "register", false);

        model_post_not_token("/register", request_2, "register", false);
        request_2.setPassword("");
        model_post_not_token("/register", request_2, "register", false);
        request_2.setPassword("1234");//4位密码
        model_post_not_token("/register", request_2, "register", false);
        request_2.setPassword("0011223456789");//13位密码
        model_post_not_token("/register", request_2, "register", false);

        model_post_not_token("/register", request_3, "register", false);
        request_3.setRealname("");
        model_post_not_token("/register", request_3, "register", false);

        model_post_not_token("/register", request_4, "register", false);
        request_4.setEmail("");
        model_post_not_token("/register", request_4, "register", false);
        request_4.setEmail("123");
        model_post_not_token("/register", request_4, "register", false);
        request_4.setEmail("123@");
        model_post_not_token("/register", request_4, "register", false);
        request_4.setEmail("123@123");
        model_post_not_token("/register", request_4, "register", false);
        request_4.setEmail("123@123.");
        model_post_not_token("/register", request_4, "register", false);
        request_4.setEmail("123@123.c");
        model_post_not_token("/register", request_4, "register", false);

        model_post_not_token("/register", request_5, "register", false);
        request_5.setRegion("");
        model_post_not_token("/register", request_5, "register", false);

        model_post_not_token("/register", request_6, "register", false);
        request_6.setOrganization("");
        model_post_not_token("/register", request_6, "register", false);

        model_post_not_token("/register", request, "register", true);

    }

    @Test
    void login() throws Exception {

        doReturn("success TEST_TOKEN").when(authService).login(eq("test_username"), eq("test_pwd"));
        doReturn("test_username").when(jwtTokenUtil).getUsernameFromToken(eq("TEST_TOKEN"));
        doReturn(new User("test_username", "test_realname", "test_pwd", "test_email", "test_region", "test_organization")).when(userDetailsService).loadUserByUsername(eq("test_username"));

        LoginRequest request_1 = new LoginRequest(null, "test_pwd");
        LoginRequest request_2 = new LoginRequest("test_username", null);
        LoginRequest request = new LoginRequest("test_username", "test_pwd");

        model_post_not_token("/login", request_1, "login", false);
        request_1.setUsername("");
        model_post_not_token("/login", request_1, "login", false);

        model_post_not_token("/login", request_2, "login", false);
//        request_2.setPassword("");
//        model_post_not_token("/login", request_2, "login", false);
//        request_2.setPassword("1234");//4位密码
//        model_post_not_token("/login", request_2, "login", false);
//        request_2.setPassword("0011223456789");//13位密码
//        model_post_not_token("/login", request_2, "login", false);

        model_post_not_token("/login", request, "login", true);
    }

    @Test
    void find_user() throws Exception {
        JSONObject result = new JSONObject();
        JSONObject result_1 = new JSONObject();
        JSONArray users = new JSONArray();
        result.put("message", "success");
        result.put("user", users);

        doReturn(result).when(authService).find_user(anyString(), eq("test_realname"));
        doReturn(result_1).when(authService).find_user(anyString(), eq("test_realname_1"));

        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_and_param("/find_user", "realname", null, "find_user", false, token);
        assertThrows(NestedServletException.class,
                ()->
                    model_get_with_token_and_param("/find_user", "realname", "", "find_user", false, token));
        model_get_with_token_and_param("/find_user", "realname","test_realname_1", "find_user", false, token);
        model_get_with_token_and_param("/find_user", "realname","test_realname", "find_user", true, token);

    }

    //与Service层集成测试
    @Test
    void my_information() throws Exception {
        String token = authService.login("test_username", "test_pwd").substring(7);
        mockMvc.perform(get("/my_information").contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(handler().handlerType(AuthController.class)) //验证执行的控制器类型
                .andExpect(handler().methodName("my_information")) //验证执行的控制器方法名
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{\"organization\":\"test_orgnazition\",\"region\":\"test_region\",\"email\":\"123@123.com\",\"realname\":\"test_realname\"}"))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void welcome() throws Exception {
        mockMvc.perform(get("/welcome").contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().handlerType(AuthController.class)) //验证执行的控制器类型
                .andExpect(handler().methodName("welcome")) //验证执行的控制器方法名
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"Welcome to 2020 Software Engineering Lab2. \"}"))
//                    .andDo(print())
                .andReturn().getResponse().getContentAsString();
    }

    private void model_post_not_token(String path, Object request, String method_name, Boolean result) throws Exception {
        if (result){
            if ("login".equals(method_name)) {
                mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                        .andExpect(handler().handlerType(AuthController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                        .andExpect(status().isOk())
//                    .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
            else {
                mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                        .andExpect(handler().handlerType(AuthController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"success\"}"))
//                    .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
        }
        else {
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                    .andExpect(handler().handlerType(AuthController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    private void model_get_with_token_and_param(String path, String param_name, String param, String method_name, Boolean result, String token) throws Exception{
        if (result){
            mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param).header("Authorization", token))
                    .andExpect(handler().handlerType(AuthController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"success\",\"user\":[]}"))
//                        .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
        else {
            mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param).header("Authorization", token))
                    .andExpect(handler().handlerType(AuthController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isBadRequest())
//                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

}