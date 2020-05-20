package fudan.se.lab2.controller.request;

import com.alibaba.fastjson.JSONArray;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class DealInvitationRequest {

    @NotNull(message = "会议id不能为null")
    @Min(value = 1, message = "会议id至少为1")
    private Long conference_id;

    @NotBlank(message = "决定不能为null,长度必须大于0")
    private String decision;

    @NotNull(message = "topics不能为null")
    @Size(min =  1, message = "至少要有一个topic")
    private JSONArray topics;

    public DealInvitationRequest(){}

    public DealInvitationRequest(Long conference_id, String decision, JSONArray topics){
        this.conference_id = conference_id;
        this.decision = decision;
        this.topics = topics;
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

    public JSONArray getTopics() {
        return topics;
    }

    public void setTopics(JSONArray topics) {
        this.topics = topics;
    }
}
