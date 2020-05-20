package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Paper;
import fudan.se.lab2.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PaperRepository extends CrudRepository<Paper, Long> {

    Optional<Paper> findById(Long authorId);

    @Query(value = "select id as paper_id,title,DATE_FORMAT(update_time,'%Y-%m-%d') as update_time,status,topics,authors " +
            "from paper where author_id=?1 and conference_id=?2",nativeQuery = true)
    List<Map<String,Object>> findPaper(Long author_id, Long conference_id);

    List<Paper> findByConferenceId(Long conference_id);

    List<Paper> findByConferenceIdAndAuthorId(Long conference_id, Long id);

    @Query(value = "select results from paper p where id=?1",nativeQuery = true)
    String findResults(Long paper_id);

    List<Paper> findByConferenceIdAndReviewersContaining(Long conference_id,String reviewer);
}

