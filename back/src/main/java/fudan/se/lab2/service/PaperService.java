package fudan.se.lab2.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fudan.se.lab2.controller.request.EditPaperRequest;
import fudan.se.lab2.controller.request.SubmitPaperRequest;
import fudan.se.lab2.controller.request.SubmitReviewRequest;
import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import fudan.se.lab2.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

@Transactional
@Service
public class PaperService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ConferenceRepository conferenceRepository;
    @Autowired
    private PaperRepository paperRepository;

    private Logger logger = Logger.getLogger("LoggingDemo");

    public Paper createPaper(Paper paper){
        //author授权
        User author = userRepository.findById(paper.getAuthorId()).get();
        Role authorRole = roleRepository.findByRoleName("ROLE_author");
        //查找是否已有作者角色
        int checkRole = userRoleRepository.findByUserIdAndConferenceId(paper.getAuthorId(),paper.getConferenceId(),(long)5);    //5:ROLE_author
        if(checkRole == 0) {
            UserRole userRole = new UserRole(author, authorRole, paper.getConferenceId());
            //System.out.println("new userrole");
            userRoleRepository.save(userRole);
        }
        //新建文章
        return paperRepository.save(paper);
//        System.out.println("Create Paper:");
    }

    /**
     * @param conferenceId Long
     * @return Conference
     */
    public Conference findConferenceById(Long conferenceId){
        Optional<Conference> result = conferenceRepository.findById(conferenceId);
        return result.orElse(null);
    }

    public Paper findPaperById(Long paperId){
        Optional<Paper> result = paperRepository.findById(paperId);
        return result.orElse(null);
    }

    public Conference findConferenceByPaperId(Long paperId) {
        Paper paper = paperRepository.findById(paperId).get();
        Conference conference = conferenceRepository.findById(paper.getConferenceId()).get();
        return conference;
    }

    public String submit_paper(String username, SubmitPaperRequest request, MultipartFile file) {
        User author = getUser(username);
        //论文投稿
        Date date = new Date();
        Conference conference = findConferenceById(request.getConference_id());
        //chair试图投稿，请求并非来自前端
        if (conference.getChairId().equals(author.getId())){
            return "You mustn't submit any paper to any conference which you act as a chair!";
        }
        //投稿已结束
        if (date.after(conference.getSubmissionDdl())){
            return "submission closed";
        }
        if (!file.isEmpty()) {
            // 文件存放服务端的位置
            String result = Tool.SubmitFile(file);
            if (result.startsWith("success")){
                createPaper(new Paper(conference.getId(), author.getId(), request.getTitle(), request.getSummary(), date, "unchecked", file.getOriginalFilename(), result.substring(8),request.getTopics().toJSONString(), request.getAuthors().toJSONString()));
                return "success";
            }
            return result;
        }
        //请求并非来自前端
        return "Failed to upload " +  file.getOriginalFilename() + " because the file was empty.";
    }

    //13.1 user在某会议投的所有稿件
    public JSONObject my_paper(String username, Long conference_id){
        User user = getUser(username);

        List<Map<String,Object>> resultList = paperRepository.findPaper(user.getId(),conference_id);
        JSONObject result = new JSONObject();
        result.put("number",resultList.size());
        JSONArray jsonArray = new JSONArray(new ArrayList<>(resultList));
        //将jsonArray格式字符串->JSONArray
        for(int i = 0; i < jsonArray.size();i++){
            JSONObject paper = jsonArray.getJSONObject(i);
            String topics = paper.getString("topics");
            String authors = paper.getString("authors");
            paper.remove("topics");
            paper.remove("authors");
            paper.put("topics",JSONArray.parseArray(topics));
            paper.put("authors",JSONArray.parseArray(authors));
        }
        result.put("papers",jsonArray);
        return result;
    }

    //13.2 修改稿件时获取信息
    public JSONObject paper_information(Long paper_id){
        JSONObject result = new JSONObject();
        Paper paper = paperRepository.findById(paper_id).get();
        result.put("paper",JSON.toJSON(paper));
        result.put("number",1);
        System.out.println(result.toString());
        return result;
    }

    //13.3 提交修改后的投稿信息
    //只能修改前端传的除了id的数据
    public String submit_edited_paper(EditPaperRequest request,String username) {

        Optional<Paper> paperOptional = paperRepository.findById(request.getPaper_id());
        if (!paperOptional.isPresent())
            return "paper not found";
        //else
        Paper paper = paperOptional.get();
        User requestUser = userRepository.findByUsername(username);
        User authorUser = userRepository.findById(paper.getAuthorId()).get();
        //不是本人的paper，不处理
        if(!requestUser.getId().equals(authorUser.getId()))
            return "no permission";

        paper.setTitle(request.getTitle());
        paper.setSummary(request.getSummary());
        paper.setAuthors(request.getAuthors().toJSONString());
        paper.setTopics(request.getTopics().toJSONString());
        paperRepository.save(paper);
        return "success";
    }

    //13.3 提交修改后的投稿信息
    //只能修改前端传的除了id的数据
    public String submit_edited_file(MultipartFile file, EditPaperRequest request,String username) throws IOException {
        String message = this.submit_edited_paper(request, username);
        if (!message.equals("success")){
            return message;
        }
        //修改文件
        Paper paper = findPaperById(request.getPaper_id());
        Files.deleteIfExists(Paths.get(paper.getFilePath()));
        if (!file.isEmpty()) {
            // 文件存放服务端的位置
            String result = Tool.SubmitFile(file);
            if (result.startsWith("success")){
                paper.setUpdateTime(new Date());
                paper.setFileName(file.getOriginalFilename());
                paper.setFilePath(result.substring(8));
                return "success";
            }
            return result;
        }
        //请求并非来自前端
        return "Failed to upload " +  file.getOriginalFilename() + " because the file was empty.";
    }

    //13.4 查看论文审核结果
    public JSONObject paper_result(Long paperId){
        JSONObject result = new JSONObject();
        Optional<Paper> paperOptional = paperRepository.findById(paperId);

        if(!paperOptional.isPresent()){
            result.put("message","paper not found");
            return result;
        }
        //评审结果未发布
        Conference conference = findConferenceByPaperId(paperId);
        if (!conference.getConferenceStage().equals("published") && !conference.getConferenceStage().equals("begin") && !conference.getConferenceStage().equals("end")){
            result.put("message", "result is not published");
            return result;
        }
        Paper paper = paperOptional.get();

        JSONArray resultArray = JSON.parseArray(paperRepository.findResults(paper.getId()));
        result.put("message", "success");
        result.put("number", resultArray.size());
        result.put("result", resultArray);
        return result;
    }

    //pc member分配到的所有稿件
    public JSONObject view_papers(String username, Long conference_id){
        User pc_member = getUser(username);
        JSONObject result = new JSONObject();
        List<Paper> papers;
        try{
            papers = paperRepository.findByConferenceIdAndReviewersContaining(conference_id,pc_member.getUsername());
        }
        catch (NullPointerException e){
            e.printStackTrace();
            result.put("number",0);
            return result;
        }

        result.put("number",papers.size());
        JSONArray paperArray = new JSONArray();

        //加工返回值
        for(Paper paper: papers){
            JSONObject paperJSON = new JSONObject();
       // for(int i = 0; i < paperArray.size();i++){
        //    JSONObject paper = paperArray.getJSONObject(i);

            //获取reviewer是否审稿
            JSONArray reviewers = JSONArray.parseArray(paper.getReviewers());
            paperJSON.put("view",false);
            for(int j = 0; j < reviewers.size();j++){
                if(reviewers.getJSONObject(j).get("username").equals(username)&&((boolean) reviewers.getJSONObject(j).get("view"))){
                    paperJSON.put("view", true);
                    break;
                }
            }
            //转换topics格式
            String topics = paper.getTopics();
            paperJSON.put("topics",JSONArray.parseArray(topics));
            paperJSON.put("paper_id",paper.getId());
            paperJSON.put("title",paper.getTitle());
            paperJSON.put("status",paper.getStatus());
            paperArray.add(paperJSON);
        }
        result.put("papers",paperArray);
        return result;
    }

    //稿件信息
    public JSONObject view_paper(Long paper_id,String username){
        JSONObject result = new JSONObject();
        Paper paper = findPaperById(paper_id);
        //如果请求人不是该paper的reviewer，不返回数据
        if(!paper.getReviewers().contains(username)) {
            result.put("message", "no permission");
            return result;
        }
        result.put("title", paper.getTitle());
        result.put("summary", paper.getSummary());
        result.put("fileName",paper.getFileName());
        result.put("message","success");
        return result;
    }

    //获取稿件（在线预览/下载接口）
    public String get_paper(HttpServletResponse response, Long paper_id,String username, boolean download){
        Paper paper = findPaperById(paper_id);
        //如果请求人不是该paper的reviewer，不返回数据
        if(!paper.getReviewers().contains(username)) {
            return "no permission";
        }
        String origin_filepath = paper.getFilePath();
        if (origin_filepath.charAt(0) == 8234){
            origin_filepath = origin_filepath.substring(1);
        }
        String filepath = origin_filepath.replace("\\", "/");
//        System.out.println("D:/usr/papers/形势与政策答题纸2020.pdf".equals(filepath));
        File file = new File(filepath);
        //激活浏览器下载
        try {
            if (download) {
                String fileName = paper.getFileName();
                response.reset();
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"));
                response.setContentType("application/pdf");
            }
        }catch (UnsupportedEncodingException e){
            logger.info(e.getMessage());
        }
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        ServletOutputStream os;
        try {
            os = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
            os.close();
        }
        catch (IOException e) {
            logger.info(e.getMessage());
            return "fail";
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    logger.info(e.getMessage());
                }
            }
        }
        return "success";
    }

    //提交审稿
    public String submit_result(String username, SubmitReviewRequest request) {
        Long paper_id = request.getPaper_id();
        Paper spe_paper = findPaperById(paper_id);
        Conference conference = findConferenceByPaperId(paper_id);
        JSONArray resultArray;
        if (spe_paper.getResults() == null){    //未审核
            resultArray = new JSONArray();
        }
        else {    //已有审核结果
            resultArray = JSON.parseArray(spe_paper.getResults());
        }
        //写入审稿结果
        JSONObject reviewResult = new JSONObject();
        reviewResult.put("score", request.getScore());
        reviewResult.put("comment", request.getComment());
        reviewResult.put("confidence", request.getConfidence());
        resultArray.add(reviewResult);
        spe_paper.setReviewResult(resultArray.toJSONString());

        //更改个人审稿状态
        JSONArray reviewers = JSONArray.parseArray(spe_paper.getReviewers());
        for(int j = 0; j < reviewers.size();j++){
            if(reviewers.getJSONObject(j).get("username").equals(username)){
                reviewers.getJSONObject(j).put("view",true);
                spe_paper.setReviewers(reviewers.toJSONString());
                break;
            }
        }

        if (resultArray.size() == 3){
            spe_paper.setStatus("checked");
        }

        List<Paper> papers = paperRepository.findByConferenceId(conference.getId());
        int flag = 0;
        for (Paper paper : papers
        ) {
            if (!("checked".equals(paper.getStatus()))) {
                flag = 1;
                break;
            }
        }
        //全部审稿结束
        if (flag == 0) {
            conference.setConferenceStage("view_end");
        }
        return "success";
    }

    protected User getUser(String username){
        return userRepository.findByUsername(username);
    }

}
