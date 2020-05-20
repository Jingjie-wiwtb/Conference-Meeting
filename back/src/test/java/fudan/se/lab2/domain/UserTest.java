package fudan.se.lab2.domain;

import fudan.se.lab2.repository.UserRepository;
import fudan.se.lab2.service.AuthService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringJUnit4ClassRunner.class)
//测试时如果涉及了数据库的操作，那么测试完成后，该操作会回滚，也就是不会改变数据库内容
//@Rollback(true)
//@Transactional//(rollbackOn = Exception.class)
@SpringBootTest
class UserTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void getAuthorities() {
        User user = authService.findUserById((long)6);
        System.out.println("role:"+user.getUserRoles().toString());
        System.out.println("auth+role"+user.getAuthorities().toString());
        Assert.assertNotNull(user.getAuthorities());
    }
}