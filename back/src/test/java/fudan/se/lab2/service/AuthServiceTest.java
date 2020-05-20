package fudan.se.lab2.service;

import com.alibaba.fastjson.JSONObject;
import fudan.se.lab2.controller.request.RegisterRequest;
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
class AuthServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private AuthService authService;


//    @Test
//    void rollBack(){
//        UserRole result = authService.createUser("invitee_username","invitee_realname", "test_pwd","test_email","test_region","test_organization");
//        Assert.assertNotNull(result);
//    }


    @Test
    void createUser() {
//        Role defaultRole = roleRepository.findByRoleName("ROLE_user");
//        Assert.assertTrue(Objects.nonNull(defaultRole));
//        User user = new User("test_username","test_pwd","test_realname", "test_email","test_region","test_organization");
//        Assert.assertTrue(Objects.nonNull(user));
//        UserRole userRole = new UserRole(user,defaultRole);
//        Assert.assertTrue(Objects.nonNull(userRole));
//        Object obj = userRoleRepository.save(userRole);
//        Assert.assertTrue(Objects.nonNull(obj));
        UserRole result = authService.createUser("try_username","test_realname", "test_pwd","test_email","test_region","test_organization");
        Assert.assertNotNull(result);
    }

    @Test
    void findUserById() {
        Assert.assertNull(authService.findUserById((long) -1));
        Assert.assertNull(authService.findUserById((long) 0));
//        Optional<Conference> res = conferenceRepository.findById((long)1);
        Assert.assertNotNull(authService.findUserById((long) 6));
    }

    @Test
    void register() {
        //Controller层做了参数校验，故Service层不需要再校验
        RegisterRequest request_1 = new RegisterRequest("test_username", "test_pwd", "test_realname", "123@123.com", "test_region", "test_organization");
        RegisterRequest request = new RegisterRequest("try_username", "test_pwd", "test_realname", "123@123.com", "test_region", "test_organization");
        Assert.assertEquals("existed account", authService.register(request_1));
        Assert.assertEquals("success", authService.register(request));
    }

    @Test
    void login() {
        Assert.assertTrue(authService.login("test_username", "test_pwd").contains("success"));
        Assert.assertEquals("wrong password", authService.login("test_username", "wrong_pwd"));
        Assert.assertEquals("user not found", authService.login("wrong_username", "wrong_pwd"));
    }

    @Test
    void getUser() {

        Assert.assertNotNull(authService.getUser("test_username"));
    }

    @Test
    void find_user() {
        //自己搜自己,有一个重名
        JSONObject test_1 = authService.find_user("test_username","test_realname");
        Assert.assertEquals(2,test_1.getJSONArray("user").size());
        //多个结果
        JSONObject test_2 = authService.find_user("invitee_username","test_realname");
        Assert.assertEquals(3,test_2.getJSONArray("user").size());
        //不重名，一个结果
        JSONObject test_3 = authService.find_user("test_username","invitee_realname");
        Assert.assertEquals(1,test_3.getJSONArray("user").size());
        //没有结果
        JSONObject test_4 = authService.find_user("test_username","no_realname");
        Assert.assertTrue(test_4.isEmpty());

    }
}