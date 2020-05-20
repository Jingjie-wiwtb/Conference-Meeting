package fudan.se.lab2.repository;


import fudan.se.lab2.domain.Conference;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public interface ConferenceRepository extends CrudRepository<Conference, Long> {
    Optional<Conference> findById(Long id);

    //10.conference_detail 会议详情
    @Query(value = "select id as conference_id,abbr,fullname,chair_name,place,DATE_FORMAT(start_time,'%Y-%m-%d') as start_time,DATE_FORMAT(end_time,'%Y-%m-%d') as end_time,DATE_FORMAT(submission_begin,'%Y-%m-%d') as submission_begin,DATE_FORMAT(submission_ddl,'%Y-%m-%d') as submission_ddl,DATE_FORMAT(publish_time,'%Y-%m-%d') as publish_time,topics, "+
            "(case when conference_stage<=>'close' then 'close' when conference_stage<=>'viewing' then 'viewing' when conference_stage<=>'view_end' then 'view_end' when conference_stage<=>'published' then 'published' when current_date<=submission_ddl then 'submission' when current_date<start_time then 'submission_end' when current_date<=end_time then 'begin' else 'end' end) as conference_stage " +
            "from conference c where id=?1",nativeQuery = true)
    List<Map<String,Object>> findConferenceDetail(Long id);

    //9.myConferenceChair 我的会议
    //+ role_name;  +audit_status = "uunprocessed"
    @Query(value = "select id as conference_id,abbr,fullname,chair_name,place,DATE_FORMAT(start_time,'%Y-%m-%d') as start_time,DATE_FORMAT(end_time,'%Y-%m-%d') as end_time,DATE_FORMAT(submission_begin,'%Y-%m-%d') as submission_begin,DATE_FORMAT(submission_ddl,'%Y-%m-%d') as submission_ddl,DATE_FORMAT(publish_time,'%Y-%m-%d') as publish_time,topics, "+
            "(case when conference_stage<=>'close' then 'close' when conference_stage<=>'viewing' then 'viewing' when conference_stage<=>'view_end' then 'view_end' when conference_stage<=>'published' then 'published' when current_date<=submission_ddl then 'submission' when current_date<start_time then 'submission_end' when current_date<=end_time then 'begin' else 'end' end) as conference_stage " +
            ",audit_status,'ROLE_chair' as role_name " +
            "from conference c where c.chair_id=:chair_id " +
            "order by FIELD(audit_status,'pass','unprocessed','fail'),id desc", nativeQuery = true)
   // @Query("select new Conference(c.id,c.abbr,c.place,c.startTime,c.endTime,c.auditStatus,c.conferenceStage) from Conference c where c.chairId=:chairId")
    List<Map<String,Object>> findByChairId(@Param("chair_id") Long chair_id);

    //14.myConferenceNotChair()  除去chairId的会议  结束的在最后，会议开始时间升序
    //3为ROLE_chair的role.id
    @Query(value = "select c.id as conference_id,c.abbr as abbr,c.fullname as fullname,c.chair_name as chair_name,c.place as place,DATE_FORMAT(c.start_time,'%Y-%m-%d') as start_time,DATE_FORMAT(c.end_time,'%Y-%m-%d') as end_time,DATE_FORMAT(c.submission_begin,'%Y-%m-%d') as submission_begin,DATE_FORMAT(c.submission_ddl,'%Y-%m-%d') as submission_ddl,DATE_FORMAT(c.publish_time,'%Y-%m-%d') as publish_time,c.topics," +
            "(case when conference_stage<=>'close' then 'close' when conference_stage<=>'viewing' then 'viewing' when conference_stage<=>'view_end' then 'view_end' when conference_stage<=>'published' then 'published' when current_date<=submission_ddl then 'submission' when current_date<start_time then 'submission_end' when current_date<=end_time then 'begin' else 'end' end) as conference_stage " +
            ",r.role_name as role_name " +
            "from user_role ur left join role r on r.id=ur.role_id left join conference c on c.id=ur.conference_id " +
            "where ur.user_id=?1 and ur.role_id <> 3 and ur.conference_id is not null and c.audit_status<>'unprocessed'" +
            "order by current_date>end_time asc,start_time asc",nativeQuery = true)
    //@Query(value = "select id,chair_name,abbr,place,start_time,end_time,conference_stage from conference c where c.chairId <> ?1 order by FIELD(conference_stage,'end'),start_time asc",nativeQuery = true)
    List<Map<String,Object>> findByChairIdNotChair(Long user_id);

    //submitting
    @Query(value = "select id as conference_id,abbr,fullname,chair_name,place,DATE_FORMAT(start_time,'%Y-%m-%d') as start_time,DATE_FORMAT(end_time,'%Y-%m-%d') as end_time,DATE_FORMAT(submission_begin,'%Y-%m-%d') as submission_begin,DATE_FORMAT(submission_ddl,'%Y-%m-%d') as submission_ddl,DATE_FORMAT(publish_time,'%Y-%m-%d') as publish_time,topics,"+
            "(case when conference_stage<=>'close' then 'close' when conference_stage<=>'viewing' then 'viewing' when conference_stage<=>'view_end' then 'view_end' when conference_stage<=>'published' then 'published' when current_date<=submission_ddl then 'submission' when current_date<start_time then 'submission_end' when current_date<=end_time then 'begin' else 'end' end) as conference_stage," +
            "if(c.chair_id=:chair_id,'true','false') as is_chair " +
            "from conference c where c.conference_stage='submission' and c.audit_status<>'unprocessed' and submission_begin<=current_date&&current_date<=submission_ddl " +
            "order by submission_ddl asc",nativeQuery = true)
  //  @Query("select new Conference(c.id,c.chairName,c.fullName,c.place,c.startTime,c.endTime) from Conference c")
    List<Map<String,Object>> findSubmittingByChairId(@Param("chair_id")Long chair_id);


    //原生mysql方法
    //12.others  除去正在投稿中的会议  结束的在最后，会议开始时间升序
    @Query(value = "select id as conference_id,abbr,fullname,chair_name,place,DATE_FORMAT(start_time,'%Y-%m-%d') as start_time,DATE_FORMAT(end_time,'%Y-%m-%d') as end_time,DATE_FORMAT(submission_begin,'%Y-%m-%d') as submission_begin,DATE_FORMAT(submission_ddl,'%Y-%m-%d') as submission_ddl,DATE_FORMAT(publish_time,'%Y-%m-%d') as publish_time,topics,"+
            "(case when conference_stage<=>'close' then 'close' when conference_stage<=>'viewing' then 'viewing' when conference_stage<=>'view_end' then 'view_end' when conference_stage<=>'published' then 'published' when current_date<=submission_ddl then 'submission' when current_date<start_time then 'submission_end' when current_date<=end_time then 'begin' else 'end' end) as conference_stage " +
            "from conference c where c.audit_status<>'unprocessed' and current_date<submission_begin||current_date>submission_ddl||conference_stage='close' " +
            "order by current_date>end_time asc ,start_time asc ",nativeQuery = true)
    List<Map<String,Object>> findByConferenceStageNotSubmit();
/*
    //备用方案1： 直接返回page,
     @Query(value = "select c from conference c where c.conference_stage <> 'submission' order by FIELD(conference_stage,'end'),start_time asc, ?#{#pageable} ", countQuery = "select count(*) from user", nativeQuery = true)
*/
/*
    //备用方案2：若FIELD方法失效，可分别查询后拼接为List
    @Query("select c from conference c where c.conferenceStage <> 'submission' and c.conference_stage <> 'end'")
    List<Conference> findStageNotSubmitOrEnd();
*/

    //17.get_conference  管理员会议查询
    @Query(value ="select id as conference_id,abbr,fullname,chair_name,place,DATE_FORMAT(start_time,'%Y-%m-%d') as start_time,DATE_FORMAT(end_time,'%Y-%m-%d') as end_time,DATE_FORMAT(submission_begin,'%Y-%m-%d') as submission_begin,DATE_FORMAT(submission_ddl,'%Y-%m-%d') as submission_ddl,DATE_FORMAT(publish_time,'%Y-%m-%d') as publish_time,topics,"+
            "(case when conference_stage<=>'close' then 'close' when conference_stage<=>'viewing' then 'viewing' when conference_stage<=>'view_end' then 'view_end' when conference_stage<=>'published' then 'published' when current_date<=submission_ddl then 'submission' when current_date<start_time then 'submission_end' when current_date<=end_time then 'begin' else 'end' end) as conference_stage " +
            "from conference c where c.audit_status='unprocessed'",nativeQuery = true)
    List<Map<String,Object>> findUnchecked();

    @Query(value = "select id as conference_id,abbr,fullname,chair_name,place,DATE_FORMAT(start_time,'%Y-%m-%d') as start_time,DATE_FORMAT(end_time,'%Y-%m-%d') as end_time,DATE_FORMAT(submission_begin,'%Y-%m-%d') as submission_begin,DATE_FORMAT(submission_ddl,'%Y-%m-%d') as submission_ddl,DATE_FORMAT(publish_time,'%Y-%m-%d') as publish_time,topics,"+
            "(case when conference_stage<=>'close' then 'close' when conference_stage<=>'viewing' then 'viewing' when conference_stage<=>'view_end' then 'view_end' when conference_stage<=>'published' then 'published' when current_date<=submission_ddl then 'submission' when current_date<start_time then 'submission_end' when current_date<=end_time then 'begin' else 'end' end) as conference_stage " +
            ",audit_status " +
            "from conference c where c.audit_status<>'unprocessed'",nativeQuery = true)
    List<Map<String,Object>> findChecked();
}

