package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


@Entity                     //实体类的注解，必须注明
@Table(name = "invitation")      //指定对应的数据库表
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Invitation implements Serializable {

    private static final long serialVersionUID = -435951296328356589L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conference_id")
    private Long conferenceId;

    @Column(name = "invitor_name")
    private String invitorName;

    @Column(name = "invitee_id")
    private Long inviteeId;

    @Column(name = "status")
    private String status;

    @Column(name = "topics")

    private String topics;//默认为null即可

    public Invitation(){}

    public Invitation(Long conferenceId, String invitorName, Long inviteeId, String status) {
        this.conferenceId = conferenceId;
        this.invitorName = invitorName;
        this.inviteeId = inviteeId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConference_id() {
        return conferenceId;
    }

    public void setConference_id(Long conferenceId) {
        this.conferenceId = conferenceId;
    }

    public String getInvitorName() {
        return invitorName;
    }

    public void setInvitorName(String invitorName) {
        this.invitorName = invitorName;
    }

    public Long getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(Long inviteeId) {
        this.inviteeId = inviteeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    //    @Transient
//    private Date updateTime;

}