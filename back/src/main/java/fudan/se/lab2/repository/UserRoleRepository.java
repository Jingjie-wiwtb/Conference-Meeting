package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Role;
import fudan.se.lab2.domain.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

    @Query(value = "select count(*) from user_role u where user_id=?1 and conference_id=?2 and role_id=?3",nativeQuery = true)
    int findByUserIdAndConferenceId(Long authorId, Long conferenceId, Long author_id);

    //count_pc()
    @Query(value = "select count(*) from user_role r where r.conference_id=?1 and r.role_id=?2",nativeQuery = true)
    int countConferenceRole(Long conference_id,Long role_id);

    //getAllPcFromTopic()
    @Query(value = "select u.username,ur.topics from user_role ur left join user u on u.id=ur.user_id where ur.conference_id=?1 and ur.role_id=?2",nativeQuery = true)
    List<Map<String,String>> findTopicsByConferenceRole(Long conference_id, Long role_id);

    //查找pcMember（包括chair）
    @Query(value = "select u.username from user_role ur left join user u on u.id=ur.user_id where ur.conference_id=?1 and (ur.role_id=3 or ur.role_id=4)",nativeQuery = true)
    List<String> findPcAndChair(Long conference_id);
}
