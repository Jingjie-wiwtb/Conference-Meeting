package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "user_role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name="user_id")   //默认为user中的主键
    private User user;

    @JsonIgnore
    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name="role_id")
    private Role role;

    @Column(name = "conference_id")
    private Long conferenceId;

    @Column(name = "topics")
    private String topics;

    public UserRole(){}

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public UserRole(User user, Role role, Long conferenceId) {
        this.user = user;
        this.role = role;
        this.conferenceId = conferenceId;
    }

    public UserRole(User user, Role role, Long conferenceId, String topics) {
        this.user = user;
        this.role = role;
        this.conferenceId = conferenceId;
        this.topics = topics;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
//
//    public Long getConferenceId() {
//        return conferenceId;
//    }
//
//    public void setConferenceId(Long conferenceId) {
//        this.conferenceId = conferenceId;
//    }
//
//    public Long getUserId() {
//        return user.getId();
//    }
//
//    public Long getRoleId() {
//        return role.getId();
//    }
}
