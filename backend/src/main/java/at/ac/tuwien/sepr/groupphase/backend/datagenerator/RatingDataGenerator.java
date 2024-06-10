package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserRating;
import at.ac.tuwien.sepr.groupphase.backend.repository.RatingRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
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

        List<ApplicationUser> applicationUsers = userRepository.findAll()
            .stream().filter(item -> !item.isBanned() && item.getVerified() && !item.getAdmin())
            .toList();

        Random random = new Random();
        for (int i = 0; i < applicationUsers.size(); i++) {
            for (int j = 0; j < applicationUsers.size(); j++) {
                if (i == j) {
                    continue;
                }
                int rating = random.nextInt(5) + 1; // generates a random integer from 1 to 5
                ratings.add(getRating(applicationUsers.get(i).getId(), applicationUsers.get(j).getId(), (float) rating));
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
