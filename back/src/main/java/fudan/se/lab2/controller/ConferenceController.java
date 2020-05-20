package fudan.se.lab2.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import fudan.se.lab2.controller.request.*;
import fudan.se.lab2.service.ConferenceService;
import fudan.se.lab2.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;

@RestController
@Table(name = "authority")      //指定对应的数据库表
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
@Validated//使对@ReuquestParam的@Min注解生效
public class ConferenceController {

    private ConferenceService conferenceService;

    @Autowired
    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/newconference", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> conference(@Validated @RequestBody ConferenceRequest request, BindingResult bindingResult) throws JSONException {
//        logger.debug("ConferenceForm: " + request.toString());
        JSONObject confirm = Tool.DealParamError(bindingResult);
        if (confirm != null){
            return new ResponseEntity<>(confirm.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        String message = conferenceService.newConference(username, request);
        return Tool.getResponseEntity(message);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/audit", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> audit(@Validated @RequestBody AuditRequest request, BindingResult bindingResult) throws JSONException {
        JSONObject confirm = Tool.DealParamError(bindingResult);
        if (confirm != null){
            return new ResponseEntity<>(confirm.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        String message = conferenceService.audit(request);
        return Tool.getResponseEntity(message);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/start_submission", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> start_submission(@Validated @RequestParam(value = "conference_id") @Min(value = 1,message = "parameter error") Long conference_id) throws JSONException {
        String message = conferenceService.start_submission(conference_id);
        return Tool.getResponseEntity(message);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/myConferenceChair",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> myConferenceChair(@Validated @RequestParam(value = "page") @Min(value = 1,message = "parameter error") int page) throws JSONException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        JSONObject result = conferenceService.myConferenceChair(username, page);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/conference_detail",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> conference_detail(@Validated @RequestParam(value = "conference_id") @Min(value = 1,message = "parameter error") Long conference_id) throws JSONException {
        JSONObject result = conferenceService.conference_detail(conference_id);
        if ("success".equals(result.getString("message"))){
            return new ResponseEntity<>(result.getJSONObject("conferences"), HttpStatus.OK);
        }
            return new ResponseEntity<>(result.toJSONString(), HttpStatus.BAD_REQUEST);

    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/submitting",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> submitting(@Validated @RequestParam(value = "page") @Min(value = 1,message = "parameter error") int page) throws JSONException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        JSONObject result = conferenceService.submitting(username,page);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/others",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> others(@Validated @RequestParam(value = "page") @Min(value = 1,message = "parameter error") int page) throws JSONException {
        JSONObject result = conferenceService.others(page);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/myConferenceNotChair",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> myConferenceNotChair(@Validated @RequestParam(value = "page") @Min(value = 1,message = "parameter error") int page) throws JSONException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        JSONObject result = conferenceService.myConferenceNotChair(username, page);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/get_conference",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> get_conference(@Validated @RequestParam(value = "type") @NotBlank String type, @Validated @RequestParam(value = "page") @Min(value = 1,message = "parameter error") int page) throws JSONException {
        Boolean ischecked;
        JSONObject result = new JSONObject();
        if (("checked").equals(type)){
            ischecked = true;
        }
        else if (("unchecked").equals(type)){
            ischecked = false;
        }
        else {
            result.put("message", "parameter error");
            return new ResponseEntity<>(result.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        result = conferenceService.get_conference(ischecked, page);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/start_view", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> start_view(@Validated @RequestParam(value = "conference_id") @Min(value = 1,message = "parameter error") Long conference_id, @Validated @RequestParam(value = "strategy") @Min(value = 1,message = "parameter error") @Max(value = 2, message = "parameter error") int strategy) throws JSONException, JsonProcessingException, NoSuchAlgorithmException {
        String result = conferenceService.start_view(conference_id, strategy);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/distribution", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> distribution(@Validated @RequestParam(value = "conference_id") @Min(value = 1,message = "parameter error") Long conference_id) throws JSONException {
        JSONObject result = conferenceService.distribution(conference_id);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/end_view", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> end_view(@Validated @RequestParam(value = "conference_id") @Min(value = 1,message = "parameter error") Long conference_id) throws JSONException {
        String message = conferenceService.end_view(conference_id);
        return Tool.getResponseEntity(message);
    }

}



