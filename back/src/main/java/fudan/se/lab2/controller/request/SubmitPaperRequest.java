package fudan.se.lab2.controller.request;


import com.alibaba.fastjson.JSONArray;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SubmitPaperRequest {

    @NotNull(message = "会议id不能为null")
    @Min(value = 1, message = "会议id至少为1")
    private Long conference_id;

    @NotBlank(message = "论文标题不能为空")
    private String title;

    @NotBlank(message = "论文摘要不能为空")
    private String summary;

    @NotNull(message = "topics不能为null")
    @Size(min =  1, message = "至少要有一个topic")
    private JSONArray topics;

    @NotNull(message = "authors不能为null")
    @Size(min = 1, message = "至少要有一个作者")
    private JSONArray authors;

    public SubmitPaperRequest(){}

    public SubmitPaperRequest(Long conference_id, String title, String summary, JSONArray topics, JSONArray authors){
        this.conference_id = conference_id;
        this.title = title;
        this.summary = summary;
        this.topics = topics;
        this.authors = authors;
    }

    public Long getConference_id(){
        return conference_id;
    }

    public String getTitle(){
        return title;
    }

    public String getSummary(){
        return summary;
    }

    public void setConference_id(Long conference_id) {
        this.conference_id = conference_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String paper_abstract) {
        this.summary = paper_abstract;
    }

    public JSONArray getTopics() {
        return topics;
    }

    public void setTopics(JSONArray topics) {
        this.topics = topics;
    }

    public JSONArray getAuthors() {
        return authors;
    }

    public void setAuthors(JSONArray authors) {
        this.authors = authors;
    }
}
