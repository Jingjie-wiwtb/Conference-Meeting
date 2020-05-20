package fudan.se.lab2.repository;
import fudan.se.lab2.domain.Invitation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation, Long> {


    Invitation findByConferenceIdAndInviteeId(Long conference_id, Long invitee_id);


    // 15.我的邀请信息invite_message()
    // "processed"放在最后，其余按照邀请创建顺序(id)降序（最新邀请在最前面）
    @Query(value = "select c.id as conference_id,c.abbr as abbr,c.fullname as fullname,c.chair_name as chair_name,c.place as place,DATE_FORMAT(c.start_time,'%Y-%m-%d') as start_time,DATE_FORMAT(c.end_time,'%Y-%m-%d') as end_time,DATE_FORMAT(c.submission_begin,'%Y-%m-%d') as submission_begin,DATE_FORMAT(c.submission_ddl,'%Y-%m-%d') as submission_ddl,DATE_FORMAT(c.publish_time,'%Y-%m-%d') as publish_time,c.topics as topics," +
            "(case when conference_stage<=>'close' then 'close' when conference_stage<=>'viewing' then 'viewing' when conference_stage<=>'view_end' then 'view_end' when conference_stage<=>'published' then 'published' when current_date<=submission_ddl then 'submission' when current_date<start_time then 'submission_end' when current_date<=end_time then 'begin' else 'end' end) as conference_stage, " +
            "i.status as status from invitation i left join conference c on c.id=i.conference_id " +
            "where i.invitee_id = ?1 " +
            "order by FIELD(i.status,'unprocessed') desc,i.id desc ",nativeQuery = true)
    List<Map<String,Object>> findByInviteeId(Long InviteeId);

    //邀请消息数量
    @Query(value = "select count(*) from invitation i where i.invitee_id=?1 and i.status<=>'unprocessed'",nativeQuery = true)
    int getInvitationNum(Long invitee_id);

    //chair查看邀请状态
    @Query(value = "select u.real_name,u.username,u.email,u.region,u.organization,i.status,i.topics " +
            "from invitation i left join user u on i.invitee_id=u.id " +
            "where i.conference_id=?1",nativeQuery = true)
    List<Map<String,Object>> findByConferenceId(Long conference_id);

}

