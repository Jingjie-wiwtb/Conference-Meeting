package fudan.se.lab2.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import fudan.se.lab2.controller.request.EditPaperRequest;
import fudan.se.lab2.controller.request.SubmitPaperRequest;
import fudan.se.lab2.controller.request.SubmitReviewRequest;
import fudan.se.lab2.security.jwt.JwtRequestFilter;
import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.service.PaperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

//系统你执行测试方法时，调用者是SpringRunnerClass类
@RunWith(SpringRunner.class)
//测试时如果涉及了数据库的操作，那么测试完成后，该操作会回滚，也就是不会改变数据库内容
@Rollback
@Transactional(rollbackOn = Exception.class)
@SpringBootTest
class PaperControllerTest {

    @Autowired
    private PaperController paperController;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private AuthService authService;

    @SpyBean
    private PaperService paperService;

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
        mockMvc = MockMvcBuilders.standaloneSetup(paperController).addFilter(jwtRequestFilter).build();
    }

    @Test
    void submit_paper() throws Exception {

        doReturn("success").when(paperService).submit_paper(anyString(), any(SubmitPaperRequest.class), any(MultipartFile.class));

        String token = authService.login("test_username", "test_pwd").substring(7);
        JSONArray topics = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");
        JSONArray authors = JSONArray.parseArray("[{\"realname\":\"xxx\",\"organization\":\"xxx\",\"region\":\"xxx\",\"email\":\"xxx\"},{\"realname\":\"xxxx\",\"organization\":\"xxxx\",\"region\":\"xxxx\",\"email\":\"xxxx\"}]");

        SubmitPaperRequest request_1 = new SubmitPaperRequest(null, "test_title", "test_summary", topics, authors);
        SubmitPaperRequest request_2 = new SubmitPaperRequest((long)-1, "test_title", "test_summary", topics, authors);
        SubmitPaperRequest request_3 = new SubmitPaperRequest((long)0, "test_title", "test_summary", topics, authors);

        SubmitPaperRequest request_4 = new SubmitPaperRequest((long)1, null, "test_summary", topics, authors);
        SubmitPaperRequest request_5 = new SubmitPaperRequest((long)1, "test_title", null, topics, authors);

        SubmitPaperRequest request_6 = new SubmitPaperRequest((long)1, "test_title", "test_summary", null, authors);
        SubmitPaperRequest request_7 = new SubmitPaperRequest((long)1, "test_title", "test_summary", new JSONArray(), authors);

        SubmitPaperRequest request_8 = new SubmitPaperRequest((long)1, "test_title", "test_summary", topics, null);
        SubmitPaperRequest request_9 = new SubmitPaperRequest((long)1, "test_title", "test_summary", topics, new JSONArray());

        SubmitPaperRequest request_10 = new SubmitPaperRequest((long)1, "", "test_summary", topics, authors);
        SubmitPaperRequest request_11 = new SubmitPaperRequest((long)1, "test_title", "", topics, authors);

        SubmitPaperRequest request = new SubmitPaperRequest((long)1, "test_title", "test_summary", topics, authors);

        model_post_with_token_file("/submit_paper", request_1, "submit_paper", false, token);
        model_post_with_token_file("/submit_paper", request_2, "submit_paper", false, token);
        model_post_with_token_file("/submit_paper", request_3, "submit_paper", false, token);
        model_post_with_token_file("/submit_paper", request_4, "submit_paper", false, token);
        model_post_with_token_file("/submit_paper", request_5, "submit_paper", false, token);
        model_post_with_token_file("/submit_paper", request_6, "submit_paper", false, token);
        model_post_with_token_file("/submit_paper", request_7, "submit_paper", false, token);
        model_post_with_token_file("/submit_paper", request_8, "submit_paper", false, token);
        model_post_with_token_file("/submit_paper", request_9, "submit_paper", false, token);
        model_post_with_token_file("/submit_paper", request_10, "submit_paper", false, token);
        model_post_with_token_file("/submit_paper", request_11, "submit_paper", false, token);

        model_post_with_token_file("/submit_paper", request, "submit_paper", true, token);
    }

    @Test
    void my_paper() throws Exception {
        JSONObject result = new JSONObject();
        result.put("number", 1);
        result.put("papers", new JSONArray());

        doReturn(result).when(paperService).my_paper(anyString(), anyLong());

        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_and_param("/my_paper", "conference_id", null, "my_paper", false, token);
        model_get_with_token_and_param("/my_paper", "conference_id", "", "my_paper", false, token);
        assertThrows(NestedServletException.class,
                ()-> model_get_with_token_and_param("/my_paper", "conference_id", "-1", "my_paper", false, token));
        assertThrows(NestedServletException.class,
                ()-> model_get_with_token_and_param("/my_paper", "conference_id", "0", "my_paper", false, token));
        model_get_with_token_and_param("/my_paper", "conference_id", "1", "my_paper", true, token);
    }

    @Test
    void paper_information() throws Exception {
        JSONObject result = new JSONObject();
        result.put("number", 1);
        result.put("papers", new JSONArray());

        doReturn(result).when(paperService).paper_information(anyLong());

        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_and_param("/paper_information", "paper_id", null, "paper_information", false, token);
        assertThrows(NestedServletException.class,
                ()-> model_get_with_token_and_param("/paper_information", "paper_id", "", "paper_information", false, token));
        assertThrows(NestedServletException.class,
                ()-> model_get_with_token_and_param("/paper_information", "paper_id", "-1", "paper_information", false, token));
        assertThrows(NestedServletException.class,
                ()-> model_get_with_token_and_param("/paper_information", "paper_id", "0", "paper_information", false, token));
        model_get_with_token_and_param("/paper_information", "paper_id", "1", "paper_information", true, token);

    }

    @Test
    void view_papers() throws Exception {
        JSONObject result = new JSONObject();
        result.put("number", 1);
        result.put("papers", new JSONArray());

        doReturn(result).when(paperService).view_papers(anyString(), anyLong());

        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_and_param("/view_papers", "conference_id", null, "view_papers", false, token);
        model_get_with_token_and_param("/view_papers", "conference_id", "", "view_papers", false, token);
        assertThrows(NestedServletException.class,
                ()-> model_get_with_token_and_param("/view_papers", "conference_id", "-1", "view_papers", false, token));
        assertThrows(NestedServletException.class,
                ()-> model_get_with_token_and_param("/view_papers", "conference_id", "0", "view_papers", false, token));
        model_get_with_token_and_param("/view_papers", "conference_id", "1", "view_papers", true, token);

    }

    @Test
    void view_paper() throws Exception {
        JSONObject result = new JSONObject();
        result.put("title", "title");
        result.put("summary", "summary");
        result.put("fileName", "fileName");
        result.put("message","success");

        doReturn(result).when(paperService).view_paper(anyLong(),anyString());
        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_and_param("/view_paper", "paper_id", null, "view_paper", false, token);
        assertThrows(NestedServletException.class,
                ()-> model_get_with_token_and_param("/view_paper", "paper_id", "", "view_paper", false, token));
        assertThrows(NestedServletException.class,
                ()-> model_get_with_token_and_param("/view_paper", "paper_id", "-1", "view_paper", false, token));
        assertThrows(NestedServletException.class,
                ()-> model_get_with_token_and_param("/view_paper", "paper_id", "0", "view_paper", false, token));
        model_get_with_token_and_param("/view_paper", "paper_id", "1", "view_paper", true, token);

    }

    @Test
    void get_paper() throws Exception {
        doReturn("success").when(paperService).get_paper(any(HttpServletResponse.class), anyLong(),anyString(), anyBoolean());
        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_and_param("/get_paper", "paper_id", null, "get_paper", false, token);
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param("/get_paper", "paper_id", "", "get_paper", false, token));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param("/get_paper", "paper_id", "-1", "get_paper", false, token));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param("/get_paper", "paper_id", "0", "get_paper", false, token));
        model_get_with_token_and_param("/get_paper", "paper_id", "1", "get_paper", true, token);
    }

    @Test

    void read_paper() throws Exception {
        doReturn("success").when(paperService).get_paper(any(HttpServletResponse.class), anyLong(),anyString(),anyBoolean());
        String token = authService.login("test_username", "test_pwd").substring(7);

        model_get_with_token_and_param("/read_paper", "paper_id", null, "read_paper", false, token);
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param("/read_paper", "paper_id", "", "read_paper", false, token));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param("/read_paper", "paper_id", "-1", "read_paper", false, token));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_with_token_and_param("/read_paper", "paper_id", "0", "read_paper", false, token));
        model_get_with_token_and_param("/read_paper", "paper_id", "1", "read_paper", true, token);
    }

    @Test
    void submit_result() throws Exception {

        doReturn("success").when(paperService).submit_result(anyString(),any(SubmitReviewRequest.class));

        SubmitReviewRequest request_1 = new SubmitReviewRequest(null, -2, "comment", 0);
        SubmitReviewRequest request_2 = new SubmitReviewRequest((long)-1, -2, "comment", 0);
        SubmitReviewRequest request_3 = new SubmitReviewRequest((long)0, -2, "comment", 0);

        SubmitReviewRequest request_4 = new SubmitReviewRequest((long)1, -3, "comment", 0);
        SubmitReviewRequest request_5 = new SubmitReviewRequest((long)1, -3, "comment", 0);

        SubmitReviewRequest request_6 = new SubmitReviewRequest((long)1, -2, null, 0);
        SubmitReviewRequest request_7 = new SubmitReviewRequest((long)1, -2, "", 0);

        SubmitReviewRequest request_8 = new SubmitReviewRequest((long)1, -2, "comment", -1);
        SubmitReviewRequest request_9 = new SubmitReviewRequest((long)1, -2, "comment", 4);

        SubmitReviewRequest request_10 = new SubmitReviewRequest((long)1, -2, "comment", 0);
        SubmitReviewRequest request_11 = new SubmitReviewRequest((long)1, -1, "comment", 1);
        SubmitReviewRequest request_12 = new SubmitReviewRequest((long)1, 1, "comment", 2);
        SubmitReviewRequest request_13 = new SubmitReviewRequest((long)1, 2, "comment", 3);

        model_post_not_token("/submit_result", request_1, "submit_result", false);
        model_post_not_token("/submit_result", request_2, "submit_result", false);
        model_post_not_token("/submit_result", request_3, "submit_result", false);
        model_post_not_token("/submit_result", request_4, "submit_result", false);
        model_post_not_token("/submit_result", request_5, "submit_result", false);
        model_post_not_token("/submit_result", request_6, "submit_result", false);
        model_post_not_token("/submit_result", request_7, "submit_result", false);
        model_post_not_token("/submit_result", request_8, "submit_result", false);
        model_post_not_token("/submit_result", request_9, "submit_result", false);

        String token = authService.login("test_username", "test_pwd").substring(7);

        model_post_with_token("/submit_result", request_10, "submit_result", true,token);
        model_post_with_token("/submit_result", request_11, "submit_result", true,token);
        model_post_with_token("/submit_result", request_12, "submit_result", true,token);
        model_post_with_token("/submit_result", request_13, "submit_result", true,token);
    }

    @Test
    void paper_result() throws Exception {
        JSONObject result = new JSONObject();
        JSONArray resultArray = new JSONArray();
        result.put("number", 1);
        result.put("result", resultArray);
        result.put("message", "success");

        doReturn(result).when(paperService).paper_result(anyLong());

        model_get_not_token_with_param("/paper_result", "paper_id", null, "paper_result", false);
        model_get_not_token_with_param("/paper_result", "paper_id", "1", "paper_result", true);
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/paper_result", "paper_id", "", "paper_result", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/paper_result", "paper_id", "-1", "paper_result", false));
        assertThrows(NestedServletException.class,
                ()->
                        model_get_not_token_with_param("/paper_result", "paper_id", "0", "paper_result", false));

    }



    @Test
    void submit_edited_paper() throws Exception {
        doReturn("success").when(paperService).submit_edited_paper(any(EditPaperRequest.class), anyString());

        String token = authService.login("test_username", "test_pwd").substring(7);

        JSONArray topics = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");
        JSONArray authors = JSONArray.parseArray("[{\"realname\":\"xxx\",\"organization\":\"xxx\",\"region\":\"xxx\",\"email\":\"xxx\"},{\"realname\":\"xxxx\",\"organization\":\"xxxx\",\"region\":\"xxxx\",\"email\":\"xxxx\"}]");

        EditPaperRequest request_1 = new EditPaperRequest(null, "title", "summary", topics, authors);
        EditPaperRequest request_2 = new EditPaperRequest((long)1, null, "summary", topics, authors);
        EditPaperRequest request_3 = new EditPaperRequest((long)1, "title", null, topics, authors);
        EditPaperRequest request_4 = new EditPaperRequest((long)1, "title", "summary", null, authors);
        EditPaperRequest request_5 = new EditPaperRequest((long)1, "title", "summary", topics, null);

        EditPaperRequest request_6 = new EditPaperRequest((long)-1, "title", "summary", topics, authors);
        EditPaperRequest request_7 = new EditPaperRequest((long)0, "title", "summary", topics, authors);

        EditPaperRequest request_8 = new EditPaperRequest((long)1, "", "summary", topics, authors);
        EditPaperRequest request_9 = new EditPaperRequest((long)1, "title", "", topics, authors);

        EditPaperRequest request_10 = new EditPaperRequest((long)1, "title", "summary", new JSONArray(), authors);
        EditPaperRequest request_11 = new EditPaperRequest((long)1, "title", "summary", topics, new JSONArray());

        EditPaperRequest request = new EditPaperRequest((long)1, "title", "summary", topics, authors);

        model_post_with_token("/submit_edited_paper", request_1, "submit_edited_paper", false, token);
        model_post_with_token("/submit_edited_paper", request_2, "submit_edited_paper", false, token);
        model_post_with_token("/submit_edited_paper", request_3, "submit_edited_paper", false, token);
        model_post_with_token("/submit_edited_paper", request_4, "submit_edited_paper", false, token);
        model_post_with_token("/submit_edited_paper", request_5, "submit_edited_paper", false, token);
        model_post_with_token("/submit_edited_paper", request_6, "submit_edited_paper", false, token);
        model_post_with_token("/submit_edited_paper", request_7, "submit_edited_paper", false, token);
        model_post_with_token("/submit_edited_paper", request_8, "submit_edited_paper", false, token);
        model_post_with_token("/submit_edited_paper", request_9, "submit_edited_paper", false, token);
        model_post_with_token("/submit_edited_paper", request_10, "submit_edited_paper", false, token);
        model_post_with_token("/submit_edited_paper", request_11, "submit_edited_paper", false, token);

        model_post_with_token("/submit_edited_paper", request, "submit_edited_paper", true, token);
    }

    @Test
    void submit_edited_file() throws Exception {
        doReturn("success").when(paperService).submit_edited_file(any(MultipartFile.class), any(EditPaperRequest.class), anyString());

        String token = authService.login("test_username", "test_pwd").substring(7);
        JSONArray topics = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");
        JSONArray authors = JSONArray.parseArray("[{\"realname\":\"xxx\",\"organization\":\"xxx\",\"region\":\"xxx\",\"email\":\"xxx\"},{\"realname\":\"xxxx\",\"organization\":\"xxxx\",\"region\":\"xxxx\",\"email\":\"xxxx\"}]");

        EditPaperRequest request_1 = new EditPaperRequest(null, "test_title", "test_summary", topics, authors);
        EditPaperRequest request_2 = new EditPaperRequest((long)-1, "test_title", "test_summary", topics, authors);
        EditPaperRequest request_3 = new EditPaperRequest((long)0, "test_title", "test_summary", topics, authors);

        EditPaperRequest request_4 = new EditPaperRequest((long)1, null, "test_summary", topics, authors);
        EditPaperRequest request_5 = new EditPaperRequest((long)1, "test_title", null, topics, authors);

        EditPaperRequest request_6 = new EditPaperRequest((long)1, "test_title", "test_summary", null, authors);
        EditPaperRequest request_7 = new EditPaperRequest((long)1, "test_title", "test_summary", new JSONArray(), authors);

        EditPaperRequest request_8 = new EditPaperRequest((long)1, "test_title", "test_summary", topics, null);
        EditPaperRequest request_9 = new EditPaperRequest((long)1, "test_title", "test_summary", topics, new JSONArray());

        EditPaperRequest request_10 = new EditPaperRequest((long)1, "", "test_summary", topics, authors);
        EditPaperRequest request_11 = new EditPaperRequest((long)1, "test_title", "", topics, authors);

        EditPaperRequest request = new EditPaperRequest((long)1, "test_title", "test_summary", topics, authors);

        model_post_with_token_file("/submit_edited_file", request_1, "submit_edited_file", false, token);
        model_post_with_token_file("/submit_edited_file", request_2, "submit_edited_file", false, token);
        model_post_with_token_file("/submit_edited_file", request_3, "submit_edited_file", false, token);
        model_post_with_token_file("/submit_edited_file", request_4, "submit_edited_file", false, token);
        model_post_with_token_file("/submit_edited_file", request_5, "submit_edited_file", false, token);
        model_post_with_token_file("/submit_edited_file", request_6, "submit_edited_file", false, token);
        model_post_with_token_file("/submit_edited_file", request_7, "submit_edited_file", false, token);
        model_post_with_token_file("/submit_edited_file", request_8, "submit_edited_file", false, token);
        model_post_with_token_file("/submit_edited_file", request_9, "submit_edited_file", false, token);
        model_post_with_token_file("/submit_edited_file", request_10, "submit_edited_file", false, token);
        model_post_with_token_file("/submit_edited_file", request_11, "submit_edited_file", false, token);

        model_post_with_token_file("/submit_edited_file", request, "submit_edited_file", true, token);

    }

    private void model_post_with_token(String path, Object request, String method_name, Boolean result, String token) throws Exception {
        if (result){
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)).header("Authorization", token))
                    .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"success\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
        else {
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)).header("Authorization", token))
                    .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    private void model_get_not_token_with_param(String path, String param_name, String param, String method_name, Boolean result) throws Exception{
        if (result) {
            mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param))
                    .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"result\":[],\"number\":1,\"message\":\"success\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
        else {
            mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param))
                    .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isBadRequest())
//                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    private void model_post_with_token_file(String path, Object request, String method_name, Boolean result, String token) throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.pdf", "text/plain", "content in file".getBytes());
        MockMultipartFile params = new MockMultipartFile("params", "", "application/json", objectMapper.writeValueAsString(request).getBytes());
//        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        if (result){
            mockMvc.perform(MockMvcRequestBuilders.multipart(path)
                    .file(file)
                    .file(params)
                    .header("Authorization", token)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"success\"}"))
                    .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isOk())
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
        else {
            mockMvc.perform(MockMvcRequestBuilders.multipart(path)
                    .file(file)
                    .file(params)
                    .header("Authorization", token)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
                    .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isBadRequest())
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    private void model_get_with_token_and_param(String path, String param_name, String param, String method_name, Boolean result, String token) throws Exception{
        if (result){
            if (method_name.equals("view_paper")){
                mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param).header("Authorization", token))
                        .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("{\"summary\":\"summary\",\"fileName\":\"fileName\",\"title\":\"title\",\"message\":\"success\"}"))
//                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
            else if (method_name.equals("read_paper") || method_name.equals("get_paper")){
                mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param).header("Authorization", token))
                        .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(status().isOk())
//                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
            else {
                mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param).header("Authorization", token))
                        .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                        .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("{\"number\":1,\"papers\":[]}"))
//                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
            }
        }
        else {
            mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON).param(param_name, param).header("Authorization", token))
                    .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(status().isBadRequest())
//                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    private void model_post_not_token(String path, Object request, String method_name, Boolean result) throws Exception {
        if (result){
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                    .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"success\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
        else {
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                    .andExpect(handler().handlerType(PaperController.class)) //验证执行的控制器类型
                    .andExpect(handler().methodName(method_name)) //验证执行的控制器方法名
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"parameter error\"}"))
//                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
    }

}