package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


@Entity                     //实体类的注解，必须注明
@Table(name = "paper")      //指定对应的数据库表
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Paper implements Serializable {

    private static final long serialVersionUID = 486735603869641943L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "conference_id")
    private Long conferenceId;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "title")
    private String title;

    @Column(name = "summary")
    private String summary;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "status")
    private String status;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "topics")
    private String topics;

    @Lob
    @Column(name = "authors",columnDefinition ="TEXT")
    private String authors;

    @Column(name = "reviewers")
    private String reviewers;

    @Column(name = "results")
    private String results;

    public Paper(){}

    public Paper(Long conferenceId, Long authorId, String title, String summary, Date updateTime, String status, String fileName, String filePath, String topics, String authors) {
        this.conferenceId = conferenceId;
        this.authorId = authorId;
        this.title = title;
        this.summary = summary;
        this.updateTime = updateTime;
        this.status = status;
        this.fileName = fileName;
        this.filePath = filePath;
        this.topics = topics;
        this.authors = authors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Long conferenceId) {
        this.conferenceId = conferenceId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date submitTime) {
        this.updateTime = submitTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getReviewers() {
        return reviewers;
    }

    public void setReviewers(String reviewers) {
        this.reviewers = reviewers;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getResults() {
        return results;
    }

    public void setReviewResult(String results) {
        this.results = results;
    }
}