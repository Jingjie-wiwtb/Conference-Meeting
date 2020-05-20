package fudan.se.lab2.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fudan.se.lab2.controller.request.AuditRequest;
import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import fudan.se.lab2.tool.Tool;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Transactional
@Service
public class ConferenceService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ConferenceRepository conferenceRepository;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private PaperRepository paperRepository;


    public UserRole createConference(Conference conference){
        //Default Settings
        conference.setAuditStatus("unprocessed");
        conference.setConferenceStage("close");
        //新建会议
        conferenceRepository.save(conference);
        //设置创建者chair角色
        User chair = userRepository.findById(conference.getChairId()).get();
        Role pcRole = roleRepository.findByRoleName("ROLE_chair");
        //chair默认所有topics
        UserRole userRole = new UserRole(chair, pcRole,conference.getId(),conference.getTopics());
        return userRoleRepository.save(userRole);
    }

    /**
     * @param conferenceId Long
     * @return Conference
     */
    public Conference findConferenceById(Long conferenceId){
        Optional<Conference> result = conferenceRepository.findById(conferenceId);
//        if(!result.isPresent())
//            return null;
//        else
//            return result.get();
        return result.orElse(null);
    }

    public HashSet<String> getAllPcFromTopic(Long conference_id, String topic){
        HashSet<String> result = new HashSet<>();
        List<Map<String,String>> pcUsernames = userRoleRepository.findTopicsByConferenceRole(conference_id,(long)4);
        for (Map<String,String> pcMember :pcUsernames) {
            if(pcMember.get("topics").contains(topic))
                result.add(pcMember.get("username"));
        }
        return result;
    }

    public HashSet<String> getAllPc(Long conference_id){
        HashSet<String> result = new HashSet<>();
        List<String> resultList = userRoleRepository.findPcAndChair(conference_id);
        for(String name:resultList)
            result.add(name);
        return result;
    }


    public int count_pc(Long conference_id){
        int count = userRoleRepository.countConferenceRole(conference_id,(long)4); //4:ROLE_pcmember
        return count;
    }

    public String newConference(String username, ConferenceRequest request) {
        User chair = getUser(username);

        Conference newConference = new Conference();
        newConference.setAbbr(request.getAbbr());
        newConference.setFullName(request.getFullName());
        newConference.setChairId(chair.getId());
        newConference.setChairName(chair.getRealname());
        newConference.setStartTime(request.getStartTime());
        newConference.setEndTime(request.getEndTime());
        newConference.setPlace(request.getPlace());
        newConference.setSubmissionDdl(request.getSubmissionDdl());
        newConference.setPublishTime(request.getPublishTime());
        newConference.setTopics(request.getTopics().toJSONString());
        createConference(newConference);
        return "success";
//        //验证author是否存在 （？
//        if(user == null){
//            //   log.warn("attempting to log in with the non-existed account");
//            return "user does not exist";
//        }else{
//                return "success";
//        }
//        //return null;
    }

    public String audit(AuditRequest request) {
        //审核
        Conference conference = findConferenceById(request.getConference_id());
        String decision = request.getDecision();
        if ("pass".equals(decision) || "fail".equals(decision)){
            conference.setAuditStatus(decision);
            return "success";
        }
        else {
            return "parameter error";
        }
    }

    public String start_submission(Long conference_id) {
        //开启投稿
        Conference conference = findConferenceById(conference_id);
        Date date = new Date();
        if (date.after(conference.getStartTime()) || date.after(conference.getSubmissionDdl()) || conference.getSubmissionBegin() != null){
            //会议已开始/投稿已结束/投稿已开启，说明请求并非来自前端
            return "invalid request";
        }
        conference.setSubmissionBegin(date);
        conference.setConferenceStage("submission");
        return "success";
    }

    // 默认排序，conference_id
    public JSONObject myConferenceChair(String username, int page){
        User chair = getUser(username);
        //通过chairId查找
        //Example<Conference> example = Example.of(new Conference(chair.getId()));
        int pageSize = 10;      //default
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Map<String,Object>> resultPage = Tool.listToPage(conferenceRepository.findByChairId(chair.getId()),pageable);
        return Tool.pageToJsonResponse(resultPage,"conferences");
    }

    public JSONObject conference_detail(Long conference_id) {

        List<Map<String,Object>> resultlist = conferenceRepository.findConferenceDetail(conference_id);
        JSONObject jsonObject = new JSONObject();
        if(resultlist.isEmpty()) {
            jsonObject.put("message", "No such conference");
            return jsonObject;
        }
        jsonObject.put("message","success");
        jsonObject.put("conferences",JSON.toJSON(resultlist.get(0)));
        System.out.println(jsonObject.toString());
        return jsonObject;
    }

    //投稿中  截止日期先后
    public JSONObject submitting(String username,int page){
        User user = getUser(username);
        int pageSize = 10;      //default

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Map<String,Object>> resultPage = Tool.listToPage(conferenceRepository.findSubmittingByChairId(user.getId()),pageable);
        return Tool.pageToJsonResponse(resultPage,"conferences");
    }
    //isChair

    //除去正在投稿中的会议  结束的在最后，会议开始时间升序
    public JSONObject others(int page){
        int pageSize = 10;      //default

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        //List<Conference> notSubmission = conferenceRepository.findByConferenceStageNotSubmit();
        Page<Map<String,Object>> resultPage = Tool.listToPage(conferenceRepository.findByConferenceStageNotSubmit(),pageable);
        return Tool.pageToJsonResponse(resultPage,"conferences");
    }


    //结束的在最后，会议开始时间升序
    public JSONObject myConferenceNotChair(String username, int page){
        User user = getUser(username);
        //在user_role中找到对应role_id,conference_id和role中对应role_name, 在conference通过chair_id找conference
        int pageSize = 10;      //default
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        List<Map<String,Object>> resultList = conferenceRepository.findByChairIdNotChair(user.getId());

        //检查去重
        Map<Integer,ArrayList<Integer>> removedList = new LinkedHashMap<>();
        for(int i = 0; i < resultList.size();i++){
        //for(Map<String,Object> conferenceMap:resultList) {
            int conference_id = Integer.parseInt(resultList.get(i).get("conference_id").toString());
            if(!removedList.containsKey(conference_id)) {    //没重复
                ArrayList<Integer> indexList = new ArrayList<>();
                indexList.add(i);
                removedList.put(conference_id, indexList);
//                System.out.println(indexList);
            }
            else {                                            //有重复
                removedList.get(conference_id).add(i);
//                System.out.println(removedList.get(conference_id).toString());
            }
        }

        int removeOffset = 0;
        for(Integer id:removedList.keySet()){
            if(removedList.get(id).size() > 1){   //有重复
                HashMap<String,Object> conferenceCopy = new HashMap<>(conferenceRepository.findConferenceDetail((long)id).get(0));
                conferenceCopy.put("role_name","[\"ROLE_author\",\"ROLE_pcmember\"]");

//                System.out.println("before remove:");
//                for(Map<String,Object> map:resultList)
//                    System.out.println("notchair():"+ map.get("conference_id") + "   " +map.get("role_name"));
                //去掉resultList中重复的两个list
                resultList.remove(removedList.get(id).get(0) - removeOffset);
 //               System.out.println("remove:"+(removedList.get(id).get(0) - removeOffset));
                removeOffset++;
//                for(Map<String,Object> map:resultList)
//                    System.out.println("notchair():"+ map.get("conference_id") + "   " +map.get("role_name"));

                resultList.remove(removedList.get(id).get(1)- removeOffset);
//               System.out.println("remove:"+(removedList.get(id).get(1) - removeOffset));
                removeOffset++;
//                System.out.println("remove:"+3);
//                for(Map<String,Object> map:resultList)
//                    System.out.println("notchair():"+ map.get("role_name"));

                resultList.add(conferenceCopy);

            }
        }
        Page<Map<String,Object>> resultPage = Tool.listToPage(resultList,pageable);
//        System.out.println("final");
//        for(Map<String,Object> map:resultList)
//            System.out.println("notchair():"+ map.get("role_name"));
        return Tool.pageToJsonResponse(resultPage,"conferences");
    }

    public JSONObject get_conference(Boolean isChecked,int page){
        int pageSize = 10;      //default
        Page<Map<String,Object>> resultPage;
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        if(isChecked)
            resultPage = Tool.listToPage(conferenceRepository.findChecked(), pageable);
        else
            resultPage = Tool.listToPage(conferenceRepository.findUnchecked(),pageable);
        return Tool.pageToJsonResponse(resultPage,"conferences");

    }

    //开启审稿
    public String start_view(Long conference_id, int strategy) throws JsonProcessingException, NoSuchAlgorithmException {
        Conference conference = findConferenceById(conference_id);
        int pc_number = count_pc(conference_id);
        if (pc_number < 2){
            return "less than 2 pc member";
        }
        if (conference.getSubmissionBegin() == null){
            return "submission is not yet open";
        }
        List<Paper> papers = paperRepository.findByConferenceId(conference_id);
        if (papers == null || papers.size() == 0){
            return "no one contributed";
        }
        //会议正在投稿中,截止投稿
        if ("submission".equals(conference.getConferenceStage())){
            conference.setSubmissionDdl(new Date());
        }
        conference.setConferenceStage("viewing");
        //按照strategy分配稿件
        Tool tool = new Tool();
        switch (strategy) {
            case 1: {
                List<Paper> allRandomPapers = new ArrayList<>();//在所有pcmembers中随机分配的paper
                for (Paper paper: papers
                     ) {
                    paper.setStatus("waiting");
//                    System.out.println(paper.getTopics());
                    JSONArray topics = JSONArray.parseArray(paper.getTopics());
                    int topicSize = topics.size();
                    HashSet<String> pc_members = new HashSet<>();
                    for (int i = 0; i < topicSize; i++){
                        HashSet<String> allPcFromTopic = getAllPcFromTopic(conference_id, topics.getString(i));
                        pc_members.addAll(allPcFromTopic);
                    }
                    if (pc_members.size() < 3){
                        allRandomPapers.add(paper);
                    }
                    else {
                        //随机抽三个人分配
                        paper.setReviewers(tool.RandomThree(pc_members).toJSONString());
                    }
                }
                //在所有pc_member中随机分配的paper
                if (!allRandomPapers.isEmpty()){
                    for (Paper paper : allRandomPapers
                         ) {
                        paper.setReviewers(tool.RandomThree(getAllPc(conference_id)).toJSONString());
                    }
                }
                break;
            }
            case 2: {
                HashSet<String> allPc = getAllPc(conference_id);
                if (allPc.size() == 3){
                    for (Paper paper : papers){
                        paper.setStatus("waiting");
                        paper.setReviewers(tool.RandomThree(allPc).toJSONString());
                    }
                }
                else {
                    tool.RandomThreeBasedOnBurden(papers, allPc);
                }
                break;
            }
            default: return "invalid strategy";
        }
        return "success";
    }

    //20 返回分配信息
    public JSONObject distribution(Long conference_id){
        Conference conference = findConferenceById(conference_id);
        JSONObject result = new JSONObject();
        JSONArray pcJsonArray = new JSONArray();
        HashSet<String> allPc = getAllPc(conference_id);
        //返回每个pc评审的paperArray
        for(String pcName:allPc) {
            JSONObject pcObject = new JSONObject();
            pcObject.put("username",pcName);
            pcObject.put("PC_name",userRepository.findByUsername(pcName).getRealname());
            JSONArray pcPapers = new JSONArray();
            List<Paper> paperList = paperRepository.findByConferenceIdAndReviewersContaining(conference_id, pcName);
            for(Paper paper:paperList) {
                JSONObject paperObject = new JSONObject();
                paperObject.put("title",paper.getTitle());
                JSONArray reviewers = JSONArray.parseArray(paper.getReviewers());
                paperObject.put("view",false);
                for(int j = 0; j < reviewers.size();j++){
                    if(reviewers.getJSONObject(j).get("username").equals(pcName) && ((boolean)reviewers.getJSONObject(j).get("view"))){
                        paperObject.put("view", true);
                        break;
                    }
                }
                pcPapers.add(paperObject);
            }
            pcObject.put("papers",pcPapers);
            pcJsonArray.add(pcObject);
        }
        result.put("pcmembers",pcJsonArray);
        result.put("number",pcJsonArray.size());
        result.put("message","success");
        return result;
    }

    //发布评审结果
    public String end_view(Long conference_id) {
        Conference conference = findConferenceById(conference_id);
        List<Paper> papers = paperRepository.findByConferenceId(conference_id);
        //全部审核完毕，发布结果
        if ("view_end".equals(conference.getConferenceStage())) {
            for (Paper paper : papers
            ) {
                paper.setStatus("published");
            }
            conference.setConferenceStage("published");
            return "success";
        }
        //未审核完毕
        return "revision has not been completed";
    }

    protected User getUser(String username){
        return userRepository.findByUsername(username);
    }

}
