package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RatingDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserRating;
import at.ac.tuwien.sepr.groupphase.backend.repository.RatingRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

@Service

public class RatingServiceImpl implements RatingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RatingRepository ratingRepository;

    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public float[] getRatingOfStudent(long student) {
        LOGGER.trace("getRatingOfStudent: {}", student);
        List<Float> ratingList = ratingRepository.findAllByRated(student);
        float[] array = new float[2];
        if (ratingList.isEmpty()) {
            LOGGER.info("Rating" + 0);
            return array;
        }
        float rating = 0;
        for (Float userRating : ratingList) {
            rating += userRating;
        }
        array[0] = rating / ratingList.size();
        array[1] = ratingList.size();
        return array;
    }

    @Override
    public void updatedRating(RatingDto ratingDto, long raterUserid) throws Exception {
        LOGGER.trace("updatedRating: {}", ratingDto);
        if (Objects.equals(raterUserid, ratingDto.ratedUserid)) {
            throw new Exception("Cannot rate your self");
        }
        UserRating rating = ratingRepository.getByRatedAndRater(ratingDto.ratedUserid, raterUserid);
        if (rating == null) {
            rating = new UserRating();
            rating.setRated(ratingDto.ratedUserid);
            rating.setRater(raterUserid);
        }
        rating.setRating(ratingDto.rating);
        ratingRepository.save(rating);
    }

    @Override
    public float getRatingFromStudent(long ratedUserId, long raterUserId) {
        LOGGER.trace("getRatingFromStudent rated: {}, rater: {}", ratedUserId, raterUserId);
        UserRating rating = ratingRepository.getByRatedAndRater(ratedUserId, raterUserId);
        if (rating != null) {
            return rating.getRating();
        }
        return -1;
    }
}
