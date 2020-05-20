package fudan.se.lab2.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fudan.se.lab2.controller.request.EditPaperRequest;
import fudan.se.lab2.controller.request.SubmitPaperRequest;
import fudan.se.lab2.controller.request.SubmitReviewRequest;
import fudan.se.lab2.domain.Paper;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


@Transactional
//系统你执行测试方法时，调用者是SpringRunnerClass类
@RunWith(SpringJUnit4ClassRunner.class)
//测试时如果涉及了数据库的操作，那么测试完成后，该操作会回滚，也就是不会改变数据库内容
//@Rollback(true)
//@Transactional//(rollbackOn = Exception.class)
@SpringBootTest
/*
@SpringBootTest注解告诉SpringBoot去寻找一个主配置类(例如带有@SpringBootApplication的配置类)，并使用它来启动
Spring应用程序上下文。SpringBootTest加载完整的应用程序并注入所有可能的bean，因此速度会很慢。
在这种情况下，不需要创建 MockMvc bean，可以直接通过RestTemplate进行请求测试(或者使用TestRestTemplate)。
 */
class PaperServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private PaperService paperService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    void findConferenceById() {
        Assert.assertNull(paperService.findConferenceById((long) -1));
        Assert.assertNull(paperService.findConferenceById((long) 0));
//        Optional<Conference> res = conferenceRepository.findById((long)1);
        Assert.assertNotNull(paperService.findConferenceById((long) 1));
    }

    @Test
    void submit_paper() {

        JSONArray topics = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");
        JSONArray authors = JSONArray.parseArray("[{\"realname\":\"xxx\",\"organization\":\"xxx\",\"region\":\"xxx\",\"email\":\"xxx\"},{\"realname\":\"xxxx\",\"organization\":\"xxxx\",\"region\":\"xxxx\",\"email\":\"xxxx\"}]");

        //参数在Controller中已校验
        SubmitPaperRequest request_1 = new SubmitPaperRequest((long)2, "test_title", "test_summary", topics, authors);
        SubmitPaperRequest request = new SubmitPaperRequest((long)1, "test_title", "test_summary", topics, authors);
        MockMultipartFile file_1 = new MockMultipartFile("file", "newFile.pdf", "text/plain", "".getBytes());
        MockMultipartFile file = new MockMultipartFile("file", "newFile.pdf", "text/plain", "content in file".getBytes());
        Assert.assertEquals("submission closed", paperService.submit_paper("new_username", request_1, file));
        Assert.assertEquals("Failed to upload " +  file_1.getOriginalFilename() + " because the file was empty.", paperService.submit_paper("new_username", request, file_1));
        Assert.assertEquals("You mustn't submit any paper to any conference which you act as a chair!", paperService.submit_paper("test_username", request, file));
//        Assert.assertEquals("success", paperService.submit_paper("new_username", request, file));
        paperService.submit_paper("new_username", request, file);
    }

    @Test
    void my_paper() {
        JSONObject test = paperService.my_paper("test_username", (long) 8);
        Assert.assertNotEquals(0, (int) test.getInteger("number"));
    }

    @Test
    void view_papers() {
        //TODO:Complete the logic
        //Assert.assertNotEquals(0,paperService.view_papers("test_username",));


        Assert.assertEquals(0,(int)paperService.view_papers("wrong_username",(long)22).getInteger("number"));

        JSONObject result_1 = paperService.view_papers("test_username",(long)22);
        Assert.assertFalse(result_1.getJSONArray("papers").getJSONObject(0).containsKey("results"));
        Assert.assertFalse(result_1.getJSONArray("papers").getJSONObject(0).getBoolean("view"));

        JSONObject result_2 = paperService.view_papers("test_b",(long)18);
        Assert.assertTrue(result_2.getJSONArray("papers").getJSONObject(0).getBoolean("view"));
    }

    @Test
    void view_paper() {
        Assert.assertEquals("test_title", paperService.view_paper((long)1,"test_username").get("title"));
        Assert.assertNotNull("test_summary", paperService.view_paper((long)1,"test_username").get("summary"));
    }

    @Test
    void get_paper() {
        MockHttpServletResponse response_1 = new MockHttpServletResponse();
        String result = paperService.get_paper(response_1, (long)1, "test_username",true);
//        Assert.assertEquals("success", result);
        Assert.assertTrue(Objects.requireNonNull(response_1.getHeader("Content-Disposition")).contains("attachment;filename="));
        Assert.assertEquals("application/pdf", response_1.getContentType());
        Assert.assertNotNull(response_1.getOutputStream());
        MockHttpServletResponse response_2 = new MockHttpServletResponse();
        MockHttpServletResponse response_3 = new MockHttpServletResponse();

        paperService.get_paper(response_2, (long)1,"test_username", false);
        paperService.get_paper(response_3, (long)1,"test_username", true);
//        Assert.assertEquals("success", paperService.get_paper(response_2, (long)1, false));
//        Assert.assertEquals("success", paperService.get_paper(response_3, (long)1, true));
    }

    @Test
    void submit_result() {
        SubmitReviewRequest request = new SubmitReviewRequest((long)1, -2, "comment", 0);
        Assert.assertEquals("success", paperService.submit_result("test_username",request));
    }

    @Test
    void getUser() {
        Assert.assertNotNull(paperService.getUser("test_username"));
    }

    //13.2
    @Test
    void paper_information() {
       // Assert.assertTrue(paperService.paper_information((long)0).isEmpty());
        Assert.assertNotNull(paperService.paper_information((long)6).getJSONObject("paper"));
    }

    //13.3
    @Test
    void submit_edited_paper() {
        JSONArray topics = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");
        JSONArray authors = JSONArray.parseArray("[{\"realname\":\"xxx\",\"organization\":\"xxx\",\"region\":\"xxx\",\"email\":\"xxx\"},{\"realname\":\"xxxx\",\"organization\":\"xxxx\",\"region\":\"xxxx\",\"email\":\"xxxx\"}]");
        EditPaperRequest request = new EditPaperRequest((long)4,"test_title","test_summary",topics,authors);
        Assert.assertEquals("success",paperService.submit_edited_paper(request,"test_username"));

        EditPaperRequest illegalRequest = new EditPaperRequest((long)0,"test_title","test_summary",topics,authors);
        Assert.assertEquals("paper not found",paperService.submit_edited_paper(illegalRequest,"test_username"));

        EditPaperRequest illegalRequest2 = new EditPaperRequest((long)4,"test_title","test_summary",topics,authors);
        Assert.assertEquals("no permission",paperService.submit_edited_paper(illegalRequest2,"new_username"));
    }

    @Test
    void submit_edited_file() throws IOException {
        JSONArray topics = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");
        JSONArray authors = JSONArray.parseArray("[{\"realname\":\"xxx\",\"organization\":\"xxx\",\"region\":\"xxx\",\"email\":\"xxx\"},{\"realname\":\"xxxx\",\"organization\":\"xxxx\",\"region\":\"xxxx\",\"email\":\"xxxx\"}]");

        EditPaperRequest request = new EditPaperRequest((long)4, "test_title", "test_summary", topics, authors);
        MockMultipartFile file_1 = new MockMultipartFile("file", "newFile.pdf", "text/plain", "".getBytes());
        MockMultipartFile file = new MockMultipartFile("file", "newFile.pdf", "text/plain", "content in file".getBytes());
        Assert.assertEquals("Failed to upload " +  file_1.getOriginalFilename() + " because the file was empty.", paperService.submit_edited_file(file_1, request, "test_username"));
//        Assert.assertEquals("success", paperService.submit_edited_file(file, request, "test_username"));
        paperService.submit_edited_file(file, request, "test_username");

    }

    //13.4
    @Test
    void paper_result() {
        Assert.assertEquals("paper not found",paperService.paper_result((long)0).getString("message"));
        Assert.assertNotEquals(java.util.Optional.of(0),paperService.paper_result((long)1).getInteger("number"));
    }


}