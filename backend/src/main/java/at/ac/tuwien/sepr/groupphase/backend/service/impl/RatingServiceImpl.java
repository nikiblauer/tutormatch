package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.UserRating;
import at.ac.tuwien.sepr.groupphase.backend.repository.RatingRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service

public class RatingServiceImpl implements RatingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RatingRepository ratingRepository;

    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public float getRatingOfStudent(long student) {
        LOGGER.trace("getRatingOfStudent: {}", student);
        List<UserRating> ratingList = ratingRepository.getAllByRated(student);
        if (ratingList.isEmpty()) {
            return 0;
        }
        float rating = 0;
        for (UserRating userRating : ratingList) {
            rating += userRating.getRating();
        }
        return rating / ratingList.size();
    }
}
