package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity                     //实体类的注解，必须注明
@Table(name = "conference")      //指定对应的数据库表
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})

public class Conference implements Serializable {

    private static final long serialVersionUID = 6377546679239075800L;

    @Id     //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) //主键生成策略：自增型
    @Column(name = "id")    //数据库对应字段名称
    private Long id;

    @Column(name = "abbr")
    private String abbr;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "chair_id")
    private Long chairId;

    @Column(name = "chair_name")
    private String chairName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "start_time")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "place")
    private String place;

    @Column(name = "submission_begin")
    private Date submissionBegin;

    @Column(name = "submission_ddl")
    private Date submissionDdl;

    @Column(name = "audit_status")
    private String auditStatus;

    @Column(name = "conference_stage")
    private String conferenceStage;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "publish_time")
    private Date publishTime;

    @Column(name = "topics")
    private String topics;

    public Conference() {}


    public Conference(String abbr, String fullName, Long chairId, String chairName, Date startTime, Date endTime, String place, Date submissionDdl, String auditStatus, String conferenceStage, Date publishTime, String topics) {
        this.abbr = abbr;
        this.fullName = fullName;
        this.chairId = chairId;
        this.chairName = chairName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.submissionDdl = submissionDdl;
        this.auditStatus = auditStatus;
        this.conferenceStage = conferenceStage;
        this.publishTime = publishTime;
        this.topics = topics;
    }

    public Long getId() {
        return id;
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

    public Long getChairId() {
        return chairId;
    }

    public void setChairId(Long chairId) {
        this.chairId = chairId;
    }

    public String getChairName() {
        return chairName;
    }

    public void setChairName(String chairName) {
        this.chairName = chairName;
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

    public Date getSubmissionBegin() {
        return submissionBegin;
    }

    public void setSubmissionBegin(Date submissionBegin) {
        this.submissionBegin = submissionBegin;
    }

    public Date getSubmissionDdl() {
        return submissionDdl;
    }

    public void setSubmissionDdl(Date submissionDdl) {
        this.submissionDdl = submissionDdl;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getConferenceStage() {
        return conferenceStage;
    }

    public void setConferenceStage(String conferenceStage) {
        this.conferenceStage = conferenceStage;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }
}
