package fudan.se.lab2.controller.request;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class InviteRequest {

    @NotNull(message = "会议id不能为null")
    @Min(value = 1, message = "会议id至少为1")
    private Long conference_id;

    @NotBlank(message = "被邀请人用户名不能为空")
    private String username;

    public InviteRequest(){}

    public InviteRequest(Long conference_id, String username){
        this.conference_id = conference_id;
        this.username = username;
    }

    public Long getConference_id(){
        return conference_id;
    }

    public String getUsername() {
        return username;
    }
}
