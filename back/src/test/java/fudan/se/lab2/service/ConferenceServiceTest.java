package fudan.se.lab2.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import fudan.se.lab2.controller.request.AuditRequest;
import fudan.se.lab2.controller.request.DealInvitationRequest;
import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.domain.UserRole;
import fudan.se.lab2.tool.Tool;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


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
class ConferenceServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private ConferenceService conferenceService;

    @Test
    void createConference() throws ParseException {
        Calendar calendar = Calendar.getInstance();

        //获取当下年月日
        int year = calendar.get(Calendar.YEAR);
        //"这里需要注意的一点,Calendar.MONTH显示的月份是由0开始的"
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);

        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

        Date test_startTime = dateFormat1.parse((year + 2) + "-" + month + "-" + day);
        Date test_endTime = dateFormat1.parse((year + 3) + "-" + month + "-" + day);
        Date test_submissionDdl = dateFormat1.parse((year + 1) + "-" + month + "-" + day);
        Date test_publishTime = dateFormat1.parse((year + 4) + "-" + month + "-" + day);
        JSONArray topics = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");

        Conference conference = new Conference("test_abbr", "test_fullname", (long)6, "test_username", test_startTime, test_endTime, "test_place", test_submissionDdl, "unprocessed", "close", test_publishTime, topics.toJSONString());

        UserRole result = conferenceService.createConference(conference);
        Assert.assertNotNull(result);
//        Object obj_1 = conferenceRepository.save(conference);
//        Assert.assertTrue(Objects.nonNull(obj_1));
//        User chair = userRepository.findById(conference.getChairId()).get();
//        Assert.assertTrue(Objects.nonNull(chair));
//        Role pcRole = roleRepository.findByRoleName("ROLE_pcmember");
//        Assert.assertTrue(Objects.nonNull(pcRole));
//        UserRole userRole = new UserRole(chair, pcRole);
//        Assert.assertTrue(Objects.nonNull(userRole));
//        Object obj_2 = userRoleRepository.save(userRole);
//        Assert.assertTrue(Objects.nonNull(obj_2));
    }

    @Test
    void findConferenceById() {
        Assert.assertNull(conferenceService.findConferenceById((long) -1));
        Assert.assertNull(conferenceService.findConferenceById((long) 0));
//        Optional<Conference> res = conferenceRepository.findById((long)1);
        Assert.assertNotNull(conferenceService.findConferenceById((long) 1));
    }

    @Test
    void newConference() throws ParseException {

        Calendar calendar = Calendar.getInstance();

        //获取当下年月日
        int year = calendar.get(Calendar.YEAR);
        //"这里需要注意的一点,Calendar.MONTH显示的月份是由0开始的"
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);

        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

        Date test_startTime = dateFormat1.parse((year + 2) + "-" + month + "-" + day);
        Date test_endTime = dateFormat1.parse((year + 3) + "-" + month + "-" + day);
        Date test_submissionDdl = dateFormat1.parse((year + 1) + "-" + month + "-" + day);
        Date test_publishTime = dateFormat1.parse((year + 4) + "-" + month + "-" + day);

        JSONArray topics = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");

        ConferenceRequest request = new ConferenceRequest("test_abbr", "test_fullname", test_startTime, test_endTime, "test_place", test_submissionDdl, test_publishTime, topics);
        Assert.assertEquals("success", conferenceService.newConference("test_username", request));;
    }

    @Test
    void audit() {
        //conference_id在Controller层已校验
        AuditRequest request_1 = new AuditRequest((long)1, null);
        AuditRequest request_2 = new AuditRequest((long)1, "");
        AuditRequest request_3 = new AuditRequest((long)1, "pas");
        AuditRequest request_4 = new AuditRequest((long)1, "passs");
        AuditRequest request_5 = new AuditRequest((long)1, "fai");
        AuditRequest request_6 = new AuditRequest((long)1, "faill");

        AuditRequest request_7 = new AuditRequest((long)1, "pass");
        AuditRequest request_8 = new AuditRequest((long)1, "fail");

        Assert.assertEquals("parameter error", conferenceService.audit(request_1));
        Assert.assertEquals("parameter error", conferenceService.audit(request_2));
        Assert.assertEquals("parameter error", conferenceService.audit(request_3));
        Assert.assertEquals("parameter error", conferenceService.audit(request_4));
        Assert.assertEquals("parameter error", conferenceService.audit(request_5));
        Assert.assertEquals("parameter error", conferenceService.audit(request_6));

        Assert.assertEquals("success", conferenceService.audit(request_7));
        Assert.assertEquals("success", conferenceService.audit(request_8));
    }

    @Test
    void start_submission() {
        Assert.assertEquals("success", conferenceService.start_submission((long)1));//会议未开始
        Assert.assertEquals("invalid request", conferenceService.start_submission((long)1));//会议已开始
        Assert.assertEquals("invalid request", conferenceService.start_submission((long)2));//投稿已结束
        Assert.assertEquals("invalid request", conferenceService.start_submission((long)3));//投稿已开始
    }

    @Test
    void myConferenceChair(){
         JSONObject result = conferenceService.myConferenceChair("test_username", 1);
        JSONObject result_1 = conferenceService.myConferenceChair("new_username", 1);
        Assert.assertNotEquals(0, (int) result.getInteger("number"));
        Assert.assertEquals(0, (int) result_1.getInteger("number"));
    }

    @Test
    void conference_detail(){
        JSONObject result = conferenceService.conference_detail((long)1);
        Assert.assertEquals("success", result.getString("message"));
        JSONObject result_1 = conferenceService.conference_detail((long)1000);
        Assert.assertEquals("No such conference", result_1.getString("message"));

        //conference_stage测试
        //已结束 end
        JSONObject test_1 = conferenceService.conference_detail((long)10);
        Assert.assertEquals("end",test_1.getJSONObject("conferences").getString("conference_stage"));
        //会议开始 begin
        JSONObject test_2 = conferenceService.conference_detail((long)9);
        Assert.assertEquals("begin",test_2.getJSONObject("conferences").getString("conference_stage"));
        //结束投稿 submission_end
        JSONObject test_3 = conferenceService.conference_detail((long)8);
        Assert.assertEquals("submission_end",test_3.getJSONObject("conferences").getString("conference_stage"));
        //投稿中 submission
        JSONObject test_4 = conferenceService.conference_detail((long)7);
        Assert.assertEquals("submission",test_4.getJSONObject("conferences").getString("conference_stage"));
        //未开始投稿 close

        JSONObject test_5 = conferenceService.conference_detail((long)6);

        System.out.println("test5:"+test_5.toString());
        System.out.println("conferenceStage:"+test_5.getJSONObject("conferences").getString("conference_stage"));

        Assert.assertEquals("close",test_5.getJSONObject("conferences").getString("conference_stage"));

    }

    @Test
    void submitting(){
        JSONObject result = conferenceService.submitting("test_username", 1);
        Assert.assertNotEquals(0, (int) result.getInteger("number"));
    }

    @Test
    void others(){
        JSONObject result = conferenceService.others(1);
        Assert.assertNotEquals(0, (int) result.getInteger("number"));
    }

    @Test
    void myConferenceNotChair(){
        JSONObject result = conferenceService.myConferenceNotChair("test_username", 1);
        System.out.println(conferenceService.myConferenceChair("test_HJJ3",1).toJSONString());
        Assert.assertNotEquals(0, (int) result.getInteger("number"));
        User user_1 = new User("test_a","test_realname","test_pwd","rea@123.com","region","organization");
    }

    @Test
    void start_view() throws JsonProcessingException, NoSuchAlgorithmException {
        Assert.assertEquals("success", conferenceService.start_view((long)61, 1));
        Assert.assertEquals("success", conferenceService.start_view((long)61, 2));
        Assert.assertEquals("invalid strategy", conferenceService.start_view((long)61, 0));
        Assert.assertEquals("less than 2 pc member", conferenceService.start_view((long)8, 2));
        Assert.assertEquals("invalid strategy", conferenceService.start_view((long)61, 3));
        Assert.assertEquals("submission is not yet open", conferenceService.start_view((long)81, 2));
        Assert.assertEquals("no one contributed", conferenceService.start_view((long)83, 1));
    }

    @Test
    void end_view() {
        Assert.assertEquals("revision has not been completed", conferenceService.end_view((long)1));
        Assert.assertEquals("success", conferenceService.end_view((long)12));
    }

    @Test
    void getUser() {

        Assert.assertNotNull(conferenceService.getUser("test_username"));
    }

    @Test
    void get_conference(){
        JSONObject result_1 = conferenceService.get_conference(true, 1);
        JSONArray conferences_1 = result_1.getJSONArray("conferences");
        JSONObject conference_1 = conferences_1.getJSONObject(0);
        JSONObject result_2 = conferenceService.get_conference(false, 1);
        JSONArray conferences_2 = result_2.getJSONArray("conferences");
        JSONObject conference_2 = conferences_2.getJSONObject(0);
        Assert.assertNotNull(result_1);
        Assert.assertNotNull(conference_1.getString("audit_status"));
        Assert.assertNotNull(result_2);
        Assert.assertNull(conference_2.getString("audit_status"));
    }

    @Test
    void count_pc() {
        int test = conferenceService.count_pc((long)9);
        Assert.assertNotEquals(0,test);
    }

    @Test
    void getAllPcFromTopic() {
        Assert.assertEquals(0,conferenceService.getAllPcFromTopic((long)1,"wrong_topic").size());
        Assert.assertNotEquals(0,conferenceService.getAllPcFromTopic((long)22,"topic1"));
    }

    @Test
    void getAllPc() {
        Assert.assertNotEquals(0,conferenceService.getAllPc((long)37));
        System.out.println(conferenceService.getAllPc((long)37));
    }

    @Test
    void distribution() {
        JSONObject pcPaper = (JSONObject)conferenceService.distribution((long)58).getJSONArray("pcmembers").get(0);
        Assert.assertEquals(0,pcPaper.getJSONArray("papers").size());
        Assert.assertNotEquals(0,conferenceService.distribution((long)87));
    }
}