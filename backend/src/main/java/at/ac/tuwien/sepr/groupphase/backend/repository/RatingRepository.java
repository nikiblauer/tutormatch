package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<UserRating, Long> {

    @Query("SELECT rating from UserRating where rated = :ratedStudent")
    List<Float> findAllByRated(@Param("ratedStudent") long ratedStudent);

    UserRating getByRatedAndRater(long ratedStudent, long ratingStudent);

    @Modifying
    @Query("DELETE from UserRating where rater = :studentId OR rated = :studentId")
    void deleteAllByStudentId(@Param("studentId") long studentId);
}
