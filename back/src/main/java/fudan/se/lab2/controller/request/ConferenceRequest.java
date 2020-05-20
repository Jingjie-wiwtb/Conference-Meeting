package fudan.se.lab2.controller.request;

import com.alibaba.fastjson.JSONArray;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class ConferenceRequest {

    @NotBlank(message = "会议简写不能为null,长度必须大于0")
    private String abbr;

    @NotBlank(message = "会议全名不能为null,长度必须大于0")
    private String fullName;

    @NotNull(message = "会议开始时间不能为null")
    @Future
    private Date startTime;

    @NotNull(message = "会议结束时间不能为null")
    @Future
    private Date endTime;

    @NotBlank(message = "会议地点不能为null,长度必须大于0")
    private String place;

    @NotNull(message = "论文投稿结束时间不能为null")
    @Future
    private Date submissionDdl;

    @NotNull(message = "评审结果发布时间不能为null")
    @Future
    private Date publishTime;

    @NotNull(message = "topics不能为null")
    @Size(min =  1, message = "至少要有一个topic")
    private JSONArray topics;

    public ConferenceRequest() {}

    public ConferenceRequest(String abbr, String fullName, Date startTime, Date endTime, String place, Date submissionDdl, Date publishTime, JSONArray topics) {
        this.abbr = abbr;
        this.fullName = fullName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.submissionDdl = submissionDdl;
        this.publishTime = publishTime;
        this.topics = topics;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getSubmissionDdl() {
        return submissionDdl;
    }

    public void setSubmissionDdl(Date submissionDdl) {
        this.submissionDdl = submissionDdl;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public JSONArray getTopics() {
        return topics;
    }

    public void setTopics(JSONArray topics) {
        this.topics = topics;
    }
}
