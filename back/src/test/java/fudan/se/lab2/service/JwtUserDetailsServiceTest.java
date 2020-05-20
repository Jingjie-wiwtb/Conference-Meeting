package fudan.se.lab2.service;

import org.springframework.security.core.userdetails.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@Rollback
@Transactional//(rollbackOn = Exception.class)
@SpringBootTest
class JwtUserDetailsServiceTest {

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername() {

        //存在
        User test_1 = (User) userDetailsService.loadUserByUsername("test_username");
        Assert.assertEquals("test_username", test_1.getUsername());
        //不存在
        assertThrows(UsernameNotFoundException.class,
                ()->
                    userDetailsService.loadUserByUsername("no_username"));
    }
}