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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

        List<ApplicationUser> applicationUsers = userRepository.findAll()
            .stream().filter(item -> !item.isBanned() && item.getVerified() && !item.getAdmin())
            .toList();

        // Generate a list of all possible indices
        List<Integer> allIndices = new ArrayList<>();
        for (int i = 0; i < applicationUsers.size(); i++) {
            allIndices.add(i);
        }

        List<UserRating> ratings = new ArrayList<>();
        Random random = new Random();

        for (ApplicationUser applicationUser : applicationUsers) {
            var ratedId = applicationUser.getId();

            // Shuffle the list for each user
            Collections.shuffle(allIndices);

            // Create a sublist of shuffled indices
            List<Integer> indicesSublist = allIndices.subList(0, Math.min(200, allIndices.size()));

            for (Integer index : indicesSublist) {
                var raterId = applicationUsers.get(index).getId();
                if (ratedId.equals(raterId)) {
                    continue;
                }
                int rating = random.nextInt(5) + 1; // generates a random integer from 1 to 5
                ratings.add(getRating(ratedId, raterId, (float) rating));
            }
        }

        // Partition the ratings list into sublists of size batchSize
        List<List<UserRating>> batches = new ArrayList<>();

        final int batchSize = 10000;
        for (int i = 0; i < ratings.size(); i += batchSize) {
            int end = Math.min(i + batchSize, ratings.size());
            List<UserRating> batchList = ratings.subList(i, end);
            batches.add(batchList);
        }

        batches.stream().parallel().forEach(ratingRepository::saveAll);

        //ratingRepository.saveAll(ratings);
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
