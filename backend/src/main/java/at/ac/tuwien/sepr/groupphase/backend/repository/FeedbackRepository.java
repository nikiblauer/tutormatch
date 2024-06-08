package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    @Query("SELECT new Feedback(id,rated,rater,feedback) from Feedback where rated = :ratedStudent")
    List<Feedback> findAllByRated(@Param("ratedStudent") long ratedStudent);

    @Query("SELECT new Feedback(id,rated,rater,feedback) from Feedback where rater = :ratingStudent")
    List<Feedback> findAllByRater(@Param("ratingStudent") long ratingStudent);

    @Query("SELECT new Feedback(id,rated,rater,feedback) from Feedback where rater = :ratingStudent AND rated = :ratedStudent")
    List<Feedback> getByRatedAndRater(@Param("ratedStudent")long ratedStudent, @Param("ratingStudent") long ratingStudent);

    @Transactional
    void deleteFeedbackById(Long id);

    @Transactional
    void deleteFeedbackByRater(Long ratingStudent);

}
