package fudan.se.lab2.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import fudan.se.lab2.controller.request.DealInvitationRequest;
import fudan.se.lab2.controller.request.InviteRequest;
import fudan.se.lab2.security.jwt.JwtRequestFilter;
import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.service.InvitationService;
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
class InvitationControllerTest {

    @Autowired
    private InvitationController invitationController;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private AuthService authService;

    @SpyBean
    private InvitationService invitationService;

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
        mockMvc = MockMvcBuilders.standaloneSetup(invitationController).addFilter(jwtRequestFilter).build();
    }

    @Test
    void invite_pc() throws Exception {

        doReturn("success").when(invitationService).invite_pc(anyString(), any(InviteRequest.class));

        String token = authService.login("test_username", "test_pwd").substring(7);

        InviteRequest request_1 = new InviteRequest(null, "test_username");
        InviteRequest request_2 = new InviteRequest((long)-1, "test_username");
        InviteRequest request_3 = new InviteRequest((long)0, "test_username");

        InviteRequest request_4 = new InviteRequest((long)1, null);
        InviteRequest request_5 = new InviteRequest((long)1, "");

        InviteRequest request = new InviteRequest((long)1, "test_username");

        model_post_with_token("/invite_pc", request_1, "invite_pc", false, token);
        model_post_with_token("/invite_pc", request_2, "invite_pc", false, token);
        model_post_with_token("/invite_pc", request_3, "invite_pc", false, token);
        model_post_with_token("/invite_pc", request_4, "invite_pc", false, token);
        model_post_with_token("/invite_pc", request_5, "invite_pc", false, token);

        model_post_with_token("/invite_pc", request, "invite_pc", true, token);

    }

    @Test
    void invite_status() throws Exception {
        JSONObject result = new JSONObject();
        JSONArray conferences = new JSONArray();
        result.put("number", 1);
        result.put("invitations", conferences);
        result.put("record", 1);

        String token = authService.login("test_username", "test_pwd").substring(7);

        doReturn(result).when(invitationService).invite_status(anyString(), anyLong(), anyInt());

        model_get_with_token_and_param_2("/invite_status", "conference_id", null, "page", "1", "invite_status", false, token);
        assertThrows(NestedServletException.class,
                ()-> model_get_with_token_and_param_2("/invite_status", "conference_id", "", "page", "1", "invite_status", false, token));
        model_get_with_token_and_param_2("/invite_status", "conference_id", "1", "page", null, "invite_status", false, token);
        model_get_with_token_and_param_2("/invite_status", "conference_id", "1", "page", "", "invite_status", false, token);
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param_2("/invite_status", "conference_id", "-1", "page", "1", "invite_status", false, token));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param_2("/invite_status", "conference_id", "0", "page", "1", "invite_status", false, token));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param_2("/invite_status", "conference_id", "1", "page", "-1", "invite_status", false, token));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param_2("/invite_status", "conference_id", "1", "page", "0", "invite_status", false, token));

        model_get_with_token_and_param_2("/invite_status", "conference_id", "1", "page", "1", "invite_status", true, token);

    }

    @Test
    void deal_invitation() throws Exception {

        doReturn("success").when(invitationService).deal_invitation(anyString(), any(DealInvitationRequest.class));

        String token = authService.login("test_username", "test_pwd").substring(7);

        JSONArray topics = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");

        DealInvitationRequest request_1 = new DealInvitationRequest(null, "accept", topics);
        DealInvitationRequest request_2 = new DealInvitationRequest((long)-1, "accept", topics);
        DealInvitationRequest request_3 = new DealInvitationRequest((long)0, "accept", topics);

        DealInvitationRequest request_4 = new DealInvitationRequest((long)1, null, topics);
        DealInvitationRequest request_5 = new DealInvitationRequest((long)1, "", topics);

        DealInvitationRequest request_6 = new DealInvitationRequest((long)1, "accept", topics);
        DealInvitationRequest request_7 = new DealInvitationRequest((long)1, "refuse", topics);

        DealInvitationRequest request_8 = new DealInvitationRequest((long)1, "accept", null);
        DealInvitationRequest request_9 = new DealInvitationRequest((long)1, "refuse", new JSONArray());

        model_post_with_token("/deal_invitation", request_1, "deal_invitation", false, token);
        model_post_with_token("/deal_invitation", request_2, "deal_invitation", false, token);
        model_post_with_token("/deal_invitation", request_3, "deal_invitation", false, token);

        model_post_with_token("/deal_invitation", request_4, "deal_invitation", false, token);
        model_post_with_token("/deal_invitation", request_5, "deal_invitation", false, token);

        model_post_with_token("/deal_invitation", request_6, "deal_invitation", true, token);
        model_post_with_token("/deal_invitation", request_7, "deal_invitation", true, token);

        model_post_with_token("/deal_invitation", request_8, "deal_invitation", false, token);
        model_post_with_token("/deal_invitation", request_9, "deal_invitation", false, token);

    }

    @Test
    void invite_message() throws Exception {
        JSONObject result = new JSONObject();
        JSONArray conferences = new JSONArray();
        result.put("number", 1);
        result.put("conferences", conferences);
        result.put("record", 1);

        doReturn(result).when(invitationService).invite_message(anyString(), anyInt());

        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_and_param("/invite_message", "page", null, "invite_message", false, token);
        model_get_with_token_and_param("/invite_message", "page", "", "invite_message", false, token);
        assertThrows(NestedServletException.class,
                ()->
                    model_get_with_token_and_param("/invite_message", "page", "-1", "invite_message", false, token));
        assertThrows(NestedServletException.class,
                ()->
                    model_get_with_token_and_param("/invite_message", "page", "-0", "invite_message", false, token));
        model_get_with_token_and_param("/invite_message", "page", "1", "invite_message", true, token);

    }

    @Test
    void message_number() throws Exception {
        JSONObject result = new JSONObject();
        result.put("message", "success");
        result.put("number", 1);

        doReturn(result).when(invitationService).message_number(anyString());

        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_not_param("/message_number", "message_number", token);
    }

    private void model_post_with_token(String path, Object request, String method_name, Boolean result, String token) throws Exception {
        if (result){
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)).header("Authorization", token))
                    .andExpect(handler().handlerType(InvitationController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"success\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
        else {
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)).header("Authorization", token))
                    .andExpect(handler().handlerType(InvitationController.class)) //验证执行的控制器类型
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
                    .andExpect(handler().handlerType(InvitationController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"number\":1,\"record\":1,\"conferences\":[]}"))
//                        .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
        else {
            mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param).header("Authorization", token))
                    .andExpect(handler().handlerType(InvitationController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isBadRequest())
//                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    private void model_get_with_token_and_param_2(String path, String param_name_1, String param_1, String param_name_2, String param_2, String method_name, Boolean result, String token) throws Exception{
        if (result){
            mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name_2, param_2).param(param_name_1, param_1).header("Authorization", token))
                    .andExpect(handler().handlerType(InvitationController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"number\":1,\"invitations\":[],\"record\":1}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
        else {
            mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name_2, param_2).param(param_name_1, param_1).header("Authorization", token))
                    .andExpect(handler().handlerType(InvitationController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isBadRequest())
//                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    private void model_get_with_token_not_param(String path, String method_name, String token) throws Exception{
        mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(handler().handlerType(InvitationController.class)) //验证执行的控制器类型
                .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{\"number\":1,\"message\":\"success\"}"))
//                    .andDo(print())
                .andReturn().getResponse().getContentAsString();
    }

}