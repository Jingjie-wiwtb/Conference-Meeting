package fudan.se.lab2.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.domain.Role;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.domain.UserRole;
import fudan.se.lab2.repository.RoleRepository;
import fudan.se.lab2.repository.UserRepository;
import fudan.se.lab2.repository.UserRoleRepository;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String register(RegisterRequest request) {
        String username = request.getUsername();
        User searchUser = userRepository.findByUsername(username);
        //用户名没有重复
        if(searchUser == null) {
        //——————————————test——————————————————————————
//            System.out.println("password:"+request.getPassword());

            CharSequence charSequence = request.getPassword();
            String password  = encode_password(charSequence);

            createUser(username,request.getRealname(),password,request.getEmail(),request.getRegion(),request.getOrganization());
            return "success";     //success
        }//    log.warn("attempting to register with an account which has already existed");

        return "existed account";     //用户名已存在
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);
//System.out.println("encode:"+ encode_password("123456"));
        if(user != null) {
    //test---------------------
//            System.out.println("User:" + user.getUsername() + "found!");
//            System.out.println("Saved Password:" + user.getPassword());
//            System.out.println("Get Password:" + encode_password(password));
            //密码验证成功
            if(user.getPassword().equals(encode_password(password))){
                //签发jwt
                String token = jwtTokenUtil.generateToken(user);
                //
         //       log.warn(user.toString() + " logged in");
                return "success" + token;
            }else{
                //       log.warn("log failed");
                return "wrong password";    //wrong password
            }
        }
        else
            return "user not found";     //
    }

   public JSONObject find_user(String username, String realname){
        JSONObject jsonObject = new JSONObject();
        User chair = getUser(username);

        List<Map<String, Object>> resultList = userRepository.findByRealname(realname,chair.getId());
        if(resultList.isEmpty())
            return jsonObject;
        JSONArray jsonarray = JSONArray.parseArray(JSON.toJSONString(resultList));
        jsonObject.put("user",jsonarray);
        return jsonObject;
    }

    //与Controller层集成测试
    public JSONObject my_information(String username){
        User user = getUser(username);
        JSONObject result = new JSONObject();
        result.put("realname", user.getRealname());
        result.put("organization", user.getOrganization());
        result.put("email", user.getEmail());
        result.put("region", user.getRegion());
        return result;
    }

    public UserRole createUser(String username, String realname,String password, String email, String region, String organization){
        //新建用户
        User user = new User(username,realname,password,email,region,organization);
        userRepository.save(user);
        //新建用户角色
        Role defaultRole = roleRepository.findByRoleName("ROLE_user");
        UserRole userRole = new UserRole(user,defaultRole);
        return userRoleRepository.save(userRole);
    }

    /**
     * @param id Long
     * @return User
     */
    public User findUserById(Long id){
        Optional<User> result = userRepository.findById(id);
//        if(!result.isPresent())
//            return null;
//        else
//            return result.get();
        return result.orElse(null);
    }

    protected User getUser(String username){
        return userRepository.findByUsername(username);
    }

    private String encode_password(CharSequence charSequence) {
        return DigestUtils.md5DigestAsHex(charSequence.toString().getBytes());
    }

}
