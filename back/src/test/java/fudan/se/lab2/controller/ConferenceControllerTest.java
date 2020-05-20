package fudan.se.lab2.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import fudan.se.lab2.controller.request.AuditRequest;
import fudan.se.lab2.controller.request.DealInvitationRequest;
import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.security.jwt.JwtRequestFilter;
import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.service.ConferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tags;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
class ConferenceControllerTest {

    @Autowired
    private ConferenceController conferenceController;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private AuthService authService;

    @SpyBean
    private ConferenceService conferenceService;

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
        mockMvc = MockMvcBuilders.standaloneSetup(conferenceController).addFilter(jwtRequestFilter).build();
    }

    @Test
    void conference() throws Exception {

        doReturn("success").when(conferenceService).newConference(anyString(), any(ConferenceRequest.class));

        String token = authService.login("test_username", "test_pwd").substring(7);

        Calendar calendar = Calendar.getInstance();

        //获取当下年月日
        int year = calendar.get(Calendar.YEAR);
        //"这里需要注意的一点,Calendar.MONTH显示的月份是由0开始的"
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);

        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

        Date past_startTime = dateFormat1.parse((year - 3) + "-" + month + "-" + day);
        Date past_endTime = dateFormat1.parse((year - 2) + "-" + month + "-" + day);
        Date past_submissionDdl = dateFormat1.parse((year - 4) + "-" + month + "-" + day);
        Date past_publishTime = dateFormat1.parse((year - 1) + "-" + month + "-" + day);

        Date test_publishTime = dateFormat1.parse((year + 4) + "-" + month + "-" + day);
        Date test_startTime = dateFormat1.parse((year + 2) + "-" + month + "-" + day);
        Date test_endTime = dateFormat1.parse((year + 3) + "-" + month + "-" + day);
        Date test_submissionDdl = dateFormat1.parse((year + 1) + "-" + month + "-" + day);
        JSONArray topics = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");

        ConferenceRequest request_1 = new ConferenceRequest(null, "test_fullname", test_startTime, test_endTime, "test_place", test_submissionDdl, test_publishTime, topics);
        ConferenceRequest request_2 = new ConferenceRequest("test_abbr", null, test_startTime, test_endTime, "test_place", test_submissionDdl, test_publishTime, topics);
        ConferenceRequest request_3 = new ConferenceRequest("test_abbr", "test_fullname", null, test_endTime, "test_place", test_submissionDdl, test_publishTime, topics);
        ConferenceRequest request_4 = new ConferenceRequest("test_abbr", "test_fullname", test_startTime, null, "test_place", test_submissionDdl, test_publishTime, topics);
        ConferenceRequest request_5 = new ConferenceRequest("test_abbr", "test_fullname", test_startTime, test_endTime, null, test_submissionDdl, test_publishTime, topics);
        ConferenceRequest request_6 = new ConferenceRequest("test_abbr", "test_fullname", test_startTime, test_endTime, "test_place", null, test_publishTime, topics);
        ConferenceRequest request_7 = new ConferenceRequest("test_abbr", "test_fullname", test_startTime, test_endTime, "test_place", test_submissionDdl, null, topics);
        ConferenceRequest request_8 = new ConferenceRequest("test_abbr", "test_fullname", test_startTime, test_endTime, "test_place", test_submissionDdl, test_publishTime, null);
        ConferenceRequest request_9 = new ConferenceRequest("test_abbr", "test_fullname", test_startTime, test_endTime, "test_place", test_submissionDdl, null, new JSONArray());


        ConferenceRequest request = new ConferenceRequest("test_abbr", "test_fullname", test_startTime, test_endTime, "test_place", test_submissionDdl, test_publishTime, topics);

        model_post_with_token("/newconference", request_1, "conference", false, token);
        request_1.setAbbr("");
        model_post_with_token("/newconference", request_1, "conference", false, token);

        model_post_with_token("/newconference", request_2, "conference", false, token);
        request_2.setFullName("");
        model_post_with_token("/newconference", request_2, "conference", false, token);

        model_post_with_token("/newconference", request_3, "conference", false, token);
        request_3.setStartTime(past_startTime);
        model_post_with_token("/newconference", request_3, "conference", false, token);

        model_post_with_token("/newconference", request_4, "conference", false, token);
        request_4.setEndTime(past_endTime);
        model_post_with_token("/newconference", request_4, "conference", false, token);

        model_post_with_token("/newconference", request_5, "conference", false, token);
        request_5.setPlace("");
        model_post_with_token("/newconference", request_5, "conference", false, token);

        model_post_with_token("/newconference", request_6, "conference", false, token);
        request_6.setSubmissionDdl(past_submissionDdl);
        model_post_with_token("/newconference", request_6, "conference", false, token);

        model_post_with_token("/newconference", request_7, "conference", false, token);
        request_7.setEndTime(past_publishTime);
        model_post_with_token("/newconference", request_7, "conference", false, token);

        model_post_with_token("/newconference", request_8, "conference", false, token);
        model_post_with_token("/newconference", request_9, "conference", false, token);

        model_post_with_token("/newconference", request, "conference", true, token);

    }

    @Test
    void audit() throws Exception {

        doReturn("success").when(conferenceService).audit(any(AuditRequest.class));

        AuditRequest request_1 = new AuditRequest(null, "pass");
        AuditRequest request_2 = new AuditRequest((long)-1, "pass");
        AuditRequest request_3 = new AuditRequest((long)0, "pass");

        AuditRequest request_4 = new AuditRequest((long)1, null);
        AuditRequest request_5 = new AuditRequest((long)1, "");

        AuditRequest request_6 = new AuditRequest((long)1, "pass");
        AuditRequest request_7 = new AuditRequest((long)1, "fail");

        model_post_not_token("/audit", request_1, "audit", false);
        model_post_not_token("/audit", request_2, "audit", false);
        model_post_not_token("/audit", request_3, "audit", false);

        model_post_not_token("/audit", request_4, "audit", false);
        model_post_not_token("/audit", request_5, "audit", false);

        model_post_not_token("/audit", request_6, "audit", true);
        model_post_not_token("/audit", request_7, "audit", true);
    }

    @Test
    void start_submission() throws Exception {

        doReturn("success").when(conferenceService).start_submission(anyLong());

        model_get_not_token_with_param("/start_submission", "conference_id", null, "start_submission", false);
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/start_submission", "conference_id", "", "start_submission", false));
        assertThrows(NestedServletException.class,
                ()->
                    model_get_not_token_with_param("/start_submission", "conference_id", "-1", "start_submission", false));
        assertThrows(NestedServletException.class,
                ()->
                    model_get_not_token_with_param("/start_submission", "conference_id", "0", "start_submission", false));
        model_get_not_token_with_param("/start_submission", "conference_id", "1", "start_submission", true);
    }

    @Test
    void myConferenceChair() throws Exception {
        JSONObject result = new JSONObject();
        JSONArray conferences = new JSONArray();
        result.put("number", 1);
        result.put("conferences", conferences);
        result.put("record", 1);

        doReturn(result).when(conferenceService).myConferenceChair(anyString(), anyInt());

        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_and_param("/myConferenceChair", "page", null, "myConferenceChair", false, token);
        model_get_with_token_and_param("/myConferenceChair", "page", "", "myConferenceChair", false, token);
        assertThrows(NestedServletException.class,
                ()->
                    model_get_with_token_and_param("/myConferenceChair", "page", "0", "myConferenceChair", false, token));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param("/myConferenceChair", "page", "-1", "myConferenceChair", false, token));
        model_get_with_token_and_param("/myConferenceChair", "page", "1", "myConferenceChair", true, token);

    }

    @Test
    void conference_detail() throws Exception {
        JSONObject result = new JSONObject();
        JSONObject detail = new JSONObject();
        result.put("message", "success");
        result.put("conferences", detail);

        doReturn(result).when(conferenceService).conference_detail(anyLong());

        model_get_not_token_with_param("/conference_detail", "conference_id", null, "conference_detail", false);
        model_get_not_token_with_param("/conference_detail", "conference_id", "", "conference_detail", false);
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/conference_detail", "conference_id", "-1", "conference_detail", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/conference_detail", "conference_id", "0", "conference_detail", false));
        model_get_not_token_with_param("/conference_detail", "conference_id", "1", "conference_detail", true);
    }

    @Test
    void submitting() throws Exception {
        JSONObject result = new JSONObject();
        JSONArray conferences = new JSONArray();
        result.put("number", 1);
        result.put("conferences", conferences);
        result.put("record", 1);
        doReturn(result).when(conferenceService).submitting(anyString(), anyInt());

        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_and_param("/submitting", "page", null, "submitting", false, token);
        model_get_with_token_and_param("/submitting", "page", "1", "submitting", true, token);
        model_get_with_token_and_param("/submitting", "page", "", "submitting", false, token);
        assertThrows(NestedServletException.class,
                ()->
                    model_get_with_token_and_param("/submitting", "page", "0", "submitting", false, token));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param("/submitting", "page", "-1", "submitting", false, token));

    }

    @Test
    void others() throws Exception {
        JSONObject result = new JSONObject();
        JSONArray conferences = new JSONArray();
        result.put("number", 1);
        result.put("conferences", conferences);
        result.put("record", 1);

        doReturn(result).when(conferenceService).others(anyInt());

        model_get_not_token_with_param("/others", "page", null, "others", false);
        model_get_not_token_with_param("/others", "page", "1", "others", true);
        model_get_not_token_with_param("/others", "page", "", "others", false);
        assertThrows(NestedServletException.class,
                ()->
                    model_get_not_token_with_param("/others", "page", "-1", "others", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/others", "page", "0", "others", false));
    }

    @Test
    void myConferenceNotChair() throws Exception {
        JSONObject result = new JSONObject();
        JSONArray conferences = new JSONArray();
        result.put("number", 1);
        result.put("conferences", conferences);
        result.put("record", 1);

        doReturn(result).when(conferenceService).myConferenceNotChair(anyString(), anyInt());

        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_and_param("/myConferenceNotChair", "page", null, "myConferenceNotChair", false, token);
        model_get_with_token_and_param("/myConferenceNotChair", "page", "", "myConferenceNotChair", false, token);
        model_get_with_token_and_param("/myConferenceNotChair", "page","1", "myConferenceNotChair", true, token);
        assertThrows(NestedServletException.class,
                ()->
                    model_get_with_token_and_param("/myConferenceNotChair", "page", "0", "myConferenceNotChair", false, token));
        assertThrows(NestedServletException.class,
                ()->
                    model_get_with_token_and_param("/myConferenceNotChair", "page", "-1", "myConferenceNotChair", false, token));

    }

    @Test
    void start_view() throws Exception {

        doReturn("success").when(conferenceService).start_view(anyLong(),anyInt());

        model_get_not_token_with_param_2("/start_view", "conference_id", null, "strategy", "1", "start_view", false);
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param_2("/start_view", "conference_id", "","strategy", "2",  "start_view", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param_2("/start_view", "conference_id", "-1","strategy", "1",  "start_view", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param_2("/start_view", "conference_id", "0","strategy", "2",  "start_view", false));

        model_get_not_token_with_param_2("/start_view", "conference_id", "1", "strategy", null, "start_view", false);
        model_get_not_token_with_param_2("/start_view", "conference_id", "1","strategy", "",  "start_view", false);
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param_2("/start_view", "conference_id", "1","strategy", "-1",  "start_view", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param_2("/start_view", "conference_id", "1","strategy", "0",  "start_view", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param_2("/start_view", "conference_id", "1","strategy", "3",  "start_view", false));

        model_get_not_token_with_param_2("/start_view", "conference_id", "1","strategy", "1",  "start_view", true);
        model_get_not_token_with_param_2("/start_view", "conference_id", "1","strategy", "2",  "start_view", true);

    }

    @Test
    void end_view() throws Exception {
        doReturn("success").when(conferenceService).end_view(anyLong());

        model_get_not_token_with_param("/end_view", "conference_id", null, "end_view", false);
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/end_view", "conference_id", "", "end_view", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/end_view", "conference_id", "-1", "end_view", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/end_view", "conference_id", "0", "end_view", false));
        model_get_not_token_with_param("/end_view", "conference_id", "1", "end_view", true);
    }

    @Test
    void get_conference() throws Exception {
        JSONObject result = new JSONObject();
        JSONArray conferences = new JSONArray();
        result.put("number", 1);
        result.put("conferences", conferences);
        result.put("record", 1);

        doReturn(result).when(conferenceService).get_conference(anyBoolean(), anyInt());

        model_get_not_token_with_param_2("/get_conference", "type", null, "page", "1", "get_conference", false);
        assertThrows(NestedServletException.class,
                ()->
                    model_get_not_token_with_param_2("/get_conference", "type", "", "page", "1", "get_conference", false));
        model_get_not_token_with_param_2("/get_conference", "type", "checked", "page", null, "get_conference", false);
        model_get_not_token_with_param_2("/get_conference", "type", "unchecked", "page", "", "get_conference", false);
        assertThrows(NestedServletException.class,
                ()->
                    model_get_not_token_with_param_2("/get_conference", "type", "unchecked", "page", "-1", "get_conference", false));
        assertThrows(NestedServletException.class,
                ()->
                    model_get_not_token_with_param_2("/get_conference", "type", "unchecked", "page", "0", "get_conference", false));
        model_get_not_token_with_param_2("/get_conference", "type", "checke", "page", "1", "get_conference", false);
        model_get_not_token_with_param_2("/get_conference", "type", "checkedd", "page", "1", "get_conference", false);
        model_get_not_token_with_param_2("/get_conference", "type", "unchecke", "page", "1", "get_conference", false);
        model_get_not_token_with_param_2("/get_conference", "type", "uncheckedd", "page", "1", "get_conference", false);
        model_get_not_token_with_param_2("/get_conference", "type", "checked", "page", "1", "get_conference", true);
        model_get_not_token_with_param_2("/get_conference", "type", "unchecked", "page", "1", "get_conference", true);

    }

    @Test
    void distribution() throws Exception {
        JSONObject result = new JSONObject();
        JSONArray pcmembers = new JSONArray();
        result.put("number", 1);
        result.put("pcmembers", pcmembers);
        result.put("message", "success");

        doReturn(result).when(conferenceService).distribution(anyLong());

        model_get_not_token_with_param("/distribution", "conference_id", null, "distribution", false);
        model_get_not_token_with_param("/distribution", "conference_id", "1", "distribution", true);
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/distribution", "conference_id", "", "distribution", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/distribution", "conference_id", "-1", "distribution", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/distribution", "conference_id", "0", "distribution", false));
    }

    private void model_post_not_token(String path, Object request, String method_name, Boolean result) throws Exception {
        if (result){
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                    .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"success\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
        else {
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                    .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    private void model_post_with_token(String path, Object request, String method_name, Boolean result, String token) throws Exception {
        if (result){
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)).header("Authorization", token))
                    .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"success\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
        else {
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)).header("Authorization", token))
                    .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    private void model_get_not_token_with_param(String path, String param_name, String param, String method_name, Boolean result) throws Exception{
        if (result){
            if (method_name.equals("start_submission") || method_name.equals("end_view")){
                mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param))
                        .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"success\"}"))
//                    .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
            else if (method_name.equals("conference_detail")){
                mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param))
                        .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("{}"))
//                    .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
            else if (method_name.equals("distribution")){
                mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param))
                        .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("{\"number\":1,\"pcmembers\":[],\"message\":\"success\"}"))
//                    .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
            else {
                mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param))
                        .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("{\"number\":1,\"record\":1,\"conferences\":[]}"))
//                    .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
        }
        else {
            mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param))
                    .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isBadRequest())
//                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    private void model_get_not_token_with_param_2(String path, String param_name_1, String param_1, String param_name_2, String param_2, String method_name, Boolean result) throws Exception{
        if (result){
            if (method_name.equals("start_view")){
                mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name_2, param_2).param(param_name_1, param_1))
                        .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"success\"}"))
//                    .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
            else {
                mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name_2, param_2).param(param_name_1, param_1))
                        .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("{\"number\":1,\"record\":1,\"conferences\":[]}"))
//                    .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
        }
        else {
            mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name_2, param_2).param(param_name_1, param_1))
                    .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isBadRequest())
//                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    private void model_get_with_token_and_param(String path, String param_name, String param, String method_name, Boolean result, String token) throws Exception{
        if (result){
            if (method_name.equals("distribution")){
                mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param).header("Authorization", token))
                        .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("{\"number\":1,\"record\":1,\"conferences\":[]}"))
//                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
        }
        else {
            mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param).header("Authorization", token))
                    .andExpect(handler().handlerType(ConferenceController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isBadRequest())
//                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

}