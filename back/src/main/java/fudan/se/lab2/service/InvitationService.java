package fudan.se.lab2.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fudan.se.lab2.controller.request.DealInvitationRequest;
import fudan.se.lab2.controller.request.InviteRequest;
import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import fudan.se.lab2.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Transactional
@Service
public class InvitationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private ConferenceRepository conferenceRepository;

    public Invitation createInvitation(Invitation invitation) {
        //Default Settings
        invitation.setStatus("unprocessed");
        //新建邀请
        return invitationRepository.save(invitation);
//       System.out.println("Create Invitation:");
    }

    /**
     * @param invitee User
     * @param conferenceId Long
     */

    public UserRole setPCmember(User invitee, Long conferenceId,String topics){
        Role pcRole = roleRepository.findByRoleName("ROLE_pcmember");
        UserRole userRole = new UserRole(invitee, pcRole, conferenceId,topics);
        return userRoleRepository.save(userRole);
    }

    public String invite_pc(String username, InviteRequest request) {
        User chair = getUser(username);
        //chair发出邀请
        String invitee = request.getUsername();
        if (invitee == null || invitee.length() == 0){
            return "parameter error";
        }
        //chair试图邀请自己，请求并非来自前端
        if (username.equals(invitee)){
            return "You mustn't invite yourself as the chair of this conference!";
        }
        User member = userRepository.findByUsername(invitee);
        Invitation invitation = invitationRepository.findByConferenceIdAndInviteeId(request.getConference_id(), member.getId());
        if (invitation != null){
            System.out.println("invitee_id : "+member.getId() + "  conference_id : " + request.getConference_id());
            System.out.println(invitation.toString());
            return "have already invited";
        }
        createInvitation(new Invitation(request.getConference_id(), chair.getRealname(), member.getId(),"st"));
        return "success";
    }

//19. chair查看邀请状态
    public JSONObject invite_status(String invitor_name, Long conference_id, int page){
        int pageSize = 10;      //default
        User requestUser = userRepository.findByUsername(invitor_name);
        Conference conference = conferenceRepository.findById(conference_id).get();
        //不是本会议的chair，直接返回
        if(conference.getChairId()!=requestUser.getId()) {
            JSONObject result = new JSONObject();
            result.put("message", "no permission");
            result.put("number",0);
            return result;
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize);     //Pageable 中 page从0开始
        Page<Map<String,Object>> resultPage = Tool.listToPage(invitationRepository.findByConferenceId(conference_id),pageable);

        JSONObject resultJson = Tool.pageToJsonResponse(resultPage,"conferences");
        System.out.println("invite_status():resultJson:"+resultJson.toJSONString());
        JSONArray resultArray = resultJson.getJSONArray("conferences");
        for(int i = 0; i < resultPage.getNumberOfElements();i++){
            JSONObject oneCon = resultArray.getJSONObject(i);
            String topics = oneCon.getString("topics");
            oneCon.remove("topics");
            oneCon.put("topics", JSONArray.parseArray(topics));
        }
        return resultJson;
    }

    public String deal_invitation(String username, DealInvitationRequest request) {
        User invitee = getUser(username);
        //处理邀请
        String decision = request.getDecision();
        Invitation invitation = invitationRepository.findByConferenceIdAndInviteeId(request.getConference_id(), invitee.getId());
        if (!("unprocessed".equals(invitation.getStatus()))){
            //邀请已被处理，请求并非来自前端
            return "invalid request";
        }
        if (decision == null){
            return "parameter error";
        }
        switch (decision){
            case "accept" : {
                invitation.setStatus(decision);
                invitation.setTopics(request.getTopics().toJSONString());
                setPCmember(invitee, request.getConference_id(), request.getTopics().toJSONString());
                break;
            }
            case "refuse" : {
                invitation.setStatus(decision);
                invitation.setTopics(request.getTopics().toJSONString());
                break;
            }
            default : {
                return "parameter error";
            }
        }
        return "success";
    }

    //"processed"放在最后，其余按照邀请时间降序（最新邀请在最前面）
    public JSONObject invite_message(String username,int page){
        User invitee = getUser(username);
        int pageSize = 10;      //default
        Pageable pageable = PageRequest.of(page - 1, pageSize);     //Pageable 中 page从0开始
        Page<Map<String,Object>> resultPage = Tool.listToPage(invitationRepository.findByInviteeId(invitee.getId()),pageable);
        return Tool.pageToJsonResponse(resultPage,"conferences");
    }

     public JSONObject message_number(String username){
        User user = getUser(username);
       // List<Invitation> invitation =  invitationRepository.findByInviteeIdAndStatusIsNot(user.getId(),"unprocessed");
        int num =  invitationRepository.getInvitationNum(user.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number",num);
        return jsonObject;
     }

    protected User getUser(String username){
        return userRepository.findByUsername(username);
    }

}
