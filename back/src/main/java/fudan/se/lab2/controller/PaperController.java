package fudan.se.lab2.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fudan.se.lab2.controller.request.*;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.service.PaperService;
import fudan.se.lab2.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Table;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Table(name = "authority")      //指定对应的数据库表
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
@Validated//使对@ReuquestParam的@Min注解生效
public class PaperController {

    private PaperService paperService;

    @Autowired
    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/submit_paper",produces = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> submit_paper(@RequestPart("file") MultipartFile file, @Validated @RequestPart("params") SubmitPaperRequest request, BindingResult bindingResult) throws JSONException {
        JSONObject confirm = Tool.DealParamError(bindingResult);
        if (confirm != null){
            return new ResponseEntity<>(confirm.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        String message = paperService.submit_paper(username, request, file);
        return Tool.getResponseEntity(message);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/my_paper",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> my_paper(@Validated @RequestParam(value = "conference_id") @Min(value = 1, message = "parameter error") Long conference_id) throws JSONException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        JSONObject result = paperService.my_paper(username, conference_id);
        return Tool.getResponseEntity(result);
    }

    //13.2
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/paper_information",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> paper_information(@Validated @RequestParam(value = "paper_id") @Min(value = 1, message = "parameter error") Long paper_id) throws JSONException {
        JSONObject result = paperService.paper_information(paper_id);
        System.out.println("paper_information--number:"+result.getInteger("number"));
        return Tool.getResponseEntity(result);
    }

    //13.3
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/submit_edited_paper",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> submit_edited_paper(@Validated @RequestBody EditPaperRequest request, BindingResult bindingResult) throws JSONException {
        JSONObject confirm = Tool.DealParamError(bindingResult);
        if (confirm != null){
            return new ResponseEntity<>(confirm.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        String result = paperService.submit_edited_paper(request,username);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/submit_edited_file",produces = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> submit_edited_file(@RequestPart("file") MultipartFile file, @Validated @RequestPart("params") EditPaperRequest request, BindingResult bindingResult) throws JSONException, IOException {
        JSONObject confirm = Tool.DealParamError(bindingResult);
        if (confirm != null){
            return new ResponseEntity<>(confirm.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        String result = paperService.submit_edited_file(file, request,username);
        return Tool.getResponseEntity(result);
    }

    //13.4
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/paper_result",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> paper_result(@Validated @RequestParam(value = "paper_id") @Min(value = 1, message = "parameter error") Long paper_id) throws JSONException {
        JSONObject result = paperService.paper_result(paper_id);
        return Tool.getResponseEntity(result);
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/view_papers",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> view_papers(@Validated @RequestParam(value = "conference_id") @Min(value = 1, message = "parameter error") Long conference_id) throws JSONException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        JSONObject result = paperService.view_papers(username, conference_id);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/view_paper",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> view_paper(@Validated @RequestParam(value = "paper_id") @Min(value = 1, message = "parameter error") Long paper_id) throws JSONException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        JSONObject result = paperService.view_paper(paper_id,username);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/get_paper",produces = "application/json")
    @ResponseBody
    public void get_paper(HttpServletResponse response, @Validated @RequestParam(value = "paper_id") @Min(value = 1, message = "parameter error") Long paper_id) throws IOException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        paperService.get_paper(response, paper_id,username, true);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/read_paper", produces = "application/json")
    @ResponseBody
    public void read_paper(HttpServletResponse response, @Validated @RequestParam(value = "paper_id") @Min(value = 1, message = "parameter error") Long paper_id) throws IOException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        paperService.get_paper(response, paper_id,username, false);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/submit_result", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> submit_result(@Validated @RequestBody SubmitReviewRequest request, BindingResult bindingResult) {
        JSONObject result = Tool.DealParamError(bindingResult);
        if (result != null){
            return new ResponseEntity<>(result.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        String message = paperService.submit_result(username,request);
        return Tool.getResponseEntity(message);
    }

}



