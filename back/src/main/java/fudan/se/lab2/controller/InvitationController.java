package fudan.se.lab2.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fudan.se.lab2.controller.request.*;
import fudan.se.lab2.service.InvitationService;
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
import javax.validation.constraints.Min;

@RestController
@Table(name = "authority")      //指定对应的数据库表
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
@Validated//使对@ReuquestParam的@Min注解生效
public class InvitationController {

    private InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/invite_pc", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> invite_pc(@Validated @RequestBody InviteRequest request, BindingResult bindingResult) throws JSONException {
        JSONObject confirm = Tool.DealParamError(bindingResult);
        if (confirm != null){
            return new ResponseEntity<>(confirm.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        String message = invitationService.invite_pc(username, request);
        return Tool.getResponseEntity(message);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/invite_status", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> invite_status(@Validated @RequestParam(value = "conference_id") @Min(value = 1,message = "parameter error") Long conference_id, @RequestParam(value = "page") @Min(value = 1,message = "parameter error") int page) throws JSONException {
        String invitor_name = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        JSONObject result = invitationService.invite_status(invitor_name, conference_id, page);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/deal_invitation", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deal_invitation(@Validated @RequestBody DealInvitationRequest request, BindingResult bindingResult) throws JSONException {
        JSONObject confirm = Tool.DealParamError(bindingResult);
        if (confirm != null){
            return new ResponseEntity<>(confirm.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        String message = invitationService.deal_invitation(username, request);
        return Tool.getResponseEntity(message);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/invite_message",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> invite_message(@Validated @RequestParam(value = "page") @Min(value = 1,message = "parameter error") int page) throws JSONException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        JSONObject result = invitationService.invite_message(username, page);
        return Tool.getResponseEntity(result);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/message_number",produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> message_number() throws JSONException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        JSONObject result = invitationService.message_number(username);
        return Tool.getResponseEntity(result);
    }

}



