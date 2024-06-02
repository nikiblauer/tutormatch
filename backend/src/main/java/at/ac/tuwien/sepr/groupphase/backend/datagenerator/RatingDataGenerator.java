package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserRating;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubjectKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.RatingRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Profile("generateData")
@Component
@DependsOn("userDataGenerator")
public class RatingDataGenerator {
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public RatingDataGenerator(UserRepository userRepository, RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    @PostConstruct
    public void generateUserSubjectRelation() {
        // check if data already exists
        if (ratingRepository.count() > 0) {
            log.info("Ratings are already generated. Skipping generation.");
            return;
        }
        log.info("Generating ratings...");

        List<UserRating> ratings = new ArrayList<>();

        List<ApplicationUser> applicationUsers = userRepository.findAll();

        Random random = new Random();
        for (int i = 0; i < applicationUsers.size(); i++) {
            if (applicationUsers.get(i).getAdmin() || !(applicationUsers.get(i).getVerified())) {
                continue;
            }
            for (int j = 0; j < applicationUsers.size(); j++) {
                if (applicationUsers.get(j).getAdmin() || !(applicationUsers.get(j).getVerified())) {
                    continue;
                }
                if (i == j) {
                    continue;
                }
                float rating = random.nextInt(10) / 2.0f + 0.5f;
                ratings.add(getRating(applicationUsers.get(i).getId(), applicationUsers.get(j).getId(), rating));
            }
        }

        ratingRepository.saveAll(ratings);
        log.info("Ratings generation completed.");
    }

    private static UserRating getRating(Long ratedId, Long raterId, Float rating) {
        var userRating = new UserRating();
        userRating.setRater(raterId);
        userRating.setRating(rating);
        userRating.setRated(ratedId);
        return userRating;
    }
}
