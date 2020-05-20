package fudan.se.lab2.controller.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AuditRequest {

    @NotNull(message = "会议id不能为null")
    @Min(value = 1, message = "会议id至少为1")
    private Long conference_id;

    @NotBlank(message = "决定不能为null,长度必须大于0")
    private String decision;

    public AuditRequest(){}

    public AuditRequest(Long conference_id, String decision){
        this.conference_id = conference_id;
        this.decision = decision;
    }

    public Long getConference_id(){
        return conference_id;
    }

    public String getDecision(){
        return decision;
    }

    public void setDecision(String decision){
        this.decision = decision;
    }
}
