package fudan.se.lab2.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fudan.se.lab2.controller.request.DealInvitationRequest;
import fudan.se.lab2.controller.request.InviteRequest;
import fudan.se.lab2.domain.Invitation;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.domain.UserRole;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


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
class InvitationServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private InvitationService invitationService;

    @Test
    void createInvitation() {
        //用于测试的conference_id=1,chair_id=6,invitee_id=7
        Invitation invitation = new Invitation((long)1, "test_realname", (long)7,"unprocessed");
        Invitation result = invitationService.createInvitation(invitation);
        Assert.assertNotNull(result);
        Assert.assertEquals(invitation, result);
//        Assert.assertTrue(Objects.nonNull(invitation));
//        Object obj = invitationRepository.save(invitation);
//        Assert.assertTrue(Objects.nonNull(obj));
    }

    @Test
    void setPCmember() {
//        Role pcRole = roleRepository.findByRoleName("ROLE_pcmember");
//        Assert.assertTrue(Objects.nonNull(pcRole));
        //用于测试：id=6的user邀请id=7的user为con_id=1的会议的PCmember
        JSONArray topics = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");
        User invitee = new User("new_username","test_realname","test_pwd","test_email","test_region","test_organization");
//        Assert.assertTrue(Objects.nonNull(invitee));
//        UserRole userRole = new UserRole(invitee, pcRole, (long)1);
//        Assert.assertTrue(Objects.nonNull(userRole));
//        Object obj = userRoleRepository.save(userRole);
//        Assert.assertTrue(Objects.nonNull(obj));
        UserRole result = invitationService.setPCmember(invitee, (long)1, topics.toJSONString());
        Assert.assertNotNull(result);
    }

    @Test
    void invite_pc() {
        //conference_id、username在Controller层已校验

        InviteRequest request_1 = new InviteRequest((long)1, null);
        InviteRequest request_2 = new InviteRequest((long)1, "");
        //chair试图邀请自己
        InviteRequest request_3 = new InviteRequest((long)1, "test_username");
        InviteRequest request_4 = new InviteRequest((long)1, "invitee_username");
        InviteRequest request_5 = new InviteRequest((long)1, "uninvited_username");

        Assert.assertEquals("parameter error", invitationService.invite_pc("test_username", request_1));
        Assert.assertEquals("parameter error", invitationService.invite_pc("test_username", request_2));
        Assert.assertEquals("You mustn't invite yourself as the chair of this conference!", invitationService.invite_pc("test_username", request_3));
        Assert.assertEquals("have already invited", invitationService.invite_pc("test_username", request_4));
        Assert.assertEquals("success", invitationService.invite_pc("test_username", request_5));
    }

    @Test
    void deal_invitation() {

        JSONArray topics_1 = JSONArray.parseArray("[\"topic1\",\"topic2\",\"topic3\"]");
        JSONArray topics_2 = new JSONArray();

        //conference_id已在Controller层校验
        DealInvitationRequest request_1 = new DealInvitationRequest((long)1, null, topics_2);
        DealInvitationRequest request_2 = new DealInvitationRequest((long)1, "", topics_2);
        DealInvitationRequest request_3 = new DealInvitationRequest((long)1, "accep", topics_1);
        DealInvitationRequest request_4 = new DealInvitationRequest((long)1, "acceptt", topics_1);
        DealInvitationRequest request_5 = new DealInvitationRequest((long)1, "refus", topics_2);
        DealInvitationRequest request_6 = new DealInvitationRequest((long)1, "refused", topics_2);

        DealInvitationRequest request_7 = new DealInvitationRequest((long)1, "accept", topics_1);
        DealInvitationRequest request_8 = new DealInvitationRequest((long)1, "refuse", topics_2);

        Assert.assertEquals("parameter error", invitationService.deal_invitation("invitee_username", request_1));
        Assert.assertEquals("parameter error", invitationService.deal_invitation("invitee_username", request_2));
        Assert.assertEquals("parameter error", invitationService.deal_invitation("invitee_username", request_3));
        Assert.assertEquals("parameter error", invitationService.deal_invitation("invitee_username", request_4));
        Assert.assertEquals("parameter error", invitationService.deal_invitation("invitee_username", request_5));
        Assert.assertEquals("parameter error", invitationService.deal_invitation("invitee_username", request_6));

        //该邀请已被处理，请求并非来自前端
        Assert.assertEquals("invalid request", invitationService.deal_invitation("new_username", request_7));
        Assert.assertEquals("invalid request", invitationService.deal_invitation("new_username", request_8));

        Assert.assertEquals("success", invitationService.deal_invitation("invitee_username", request_7));
        Assert.assertEquals("success", invitationService.deal_invitation("invitee2_username", request_8));
        }

    @Test
    void invite_message(){
        JSONObject result = invitationService.invite_message("test_username", 1);
        JSONObject result_1 = invitationService.invite_message("new_username", 1);
        Assert.assertNotEquals(0, (int) result.getInteger("number"));
        Assert.assertEquals(6, (int) result_1.getInteger("number"));
    }

    @Test
    void getUser() {

        Assert.assertNotNull(invitationService.getUser("test_username"));
    }

    @Test
    void message_number() {
        Assert.assertEquals(5,(int)invitationService.message_number("new_username").getInteger("number"));
    }

    @Test
    void invite_status() {

        JSONObject test = invitationService.invite_status("test_username",(long)1,1);
        JSONArray invitation = test.getJSONArray("conferences");
        Assert.assertNotNull(invitation);
        //不是test_realname创建的
        JSONObject test_empty = invitationService.invite_status("test_username",(long)60,1);
        Assert.assertEquals(0,(int)test_empty.getInteger("number"));
    }
}