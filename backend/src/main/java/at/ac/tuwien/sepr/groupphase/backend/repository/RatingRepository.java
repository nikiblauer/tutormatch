package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<UserRating, Long> {
    List<UserRating> getAllByRated(long ratedStudent);

    UserRating getByRatedAndRater(long ratedStudent, long ratingStudent);
}
