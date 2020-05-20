package fudan.se.lab2.controller.request;

import com.alibaba.fastjson.JSONArray;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EditPaperRequest {
    @NotNull(message = "paper_id不能为null")
    @Min(value = 1, message = "paper_id至少为1")
    private Long paper_id;

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

    //需要有默认构造函数，才能json序列化
    public EditPaperRequest() {}

    public EditPaperRequest(Long paper_id, String title, String summary, JSONArray topics, JSONArray authors) {
        this.paper_id = paper_id;
        this.title = title;
        this.summary = summary;
        this.topics = topics;
        this.authors = authors;
    }

    public Long getPaper_id() {
        return paper_id;
    }

    public void setPaper_id(Long paper_id) {
        this.paper_id = paper_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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
