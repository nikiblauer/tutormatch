package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackDtoNamed;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RatingDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserRating;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.FeedbackRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RatingRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatRoomRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.RatingService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RatingServiceImpl implements RatingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RatingRepository ratingRepository;
    private final FeedbackRepository feedbackRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;


    public RatingServiceImpl(RatingRepository ratingRepository, FeedbackRepository feedbackRepository, ChatRoomRepository chatRoomRepository, UserService userService) {
        this.ratingRepository = ratingRepository;
        this.feedbackRepository = feedbackRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userService = userService;
    }


    /**
     * Retrieves the ratings for a list of students.
     *
     * @param studentIds List of student IDs to retrieve ratings for.
     * @return A map of student ID to an array where the first element is the average rating and the second element is the count of ratings.
     */
    public Map<Long, float[]> getRatingsOfStudents(List<Long> studentIds) {
        LOGGER.trace("getRatingsOfStudents: {}", studentIds);

        // Fetch aggregated ratings from the repository
        List<Object[]> results = ratingRepository.findAggregatedRatingsByRatedStudents(studentIds);
        Map<Long, float[]> ratingsMap = new HashMap<>();

        // Process the query results
        for (Object[] result : results) {
            long studentId = (long) result[0];
            float averageRating = ((Double) result[1]).floatValue();
            long count = (long) result[2];

            ratingsMap.put(studentId, new float[]{averageRating, count});
        }

        // Ensure all requested students are present in the result map
        for (Long studentId : studentIds) {
            ratingsMap.putIfAbsent(studentId, new float[]{0, 0});
        }

        return ratingsMap;
    }

    @Override
    public float[] getRatingOfStudent(long student) {
        LOGGER.trace("getRatingOfStudent: {}", student);
        List<Float> ratingList = ratingRepository.findAllByRated(student);
        float[] array = new float[2];

        if (ratingList.isEmpty()) {
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
    public void updatedRating(RatingDto ratingDto, long ratingUserId) throws Exception {
        LOGGER.trace("updatedRating: {}", ratingDto);
        if (Objects.equals(ratingUserId, ratingDto.ratedUserid)) {
            throw new Exception("Cannot rate your self");
        }
        UserRating rating = ratingRepository.getByRatedAndRater(ratingDto.ratedUserid, ratingUserId);
        if (rating == null) {
            rating = new UserRating();
            rating.setRated(ratingDto.ratedUserid);
            rating.setRater(ratingUserId);
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

    @Override
    public FeedbackDto[] getFeedbackOfStudent(long student) {
        LOGGER.trace("getFeedbackOfStudent: {}", student);
        List<Feedback> feedbackList = feedbackRepository.findAllByRated(student);
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(feedbackList, FeedbackDto[].class);
    }

    @Override
    public FeedbackDtoNamed[] getFeedbackByStudent(long student) {
        LOGGER.trace("getFeedbackByStudent: {}", student);
        List<Feedback> feedbackList = feedbackRepository.findAllByRater(student);
        ArrayList<FeedbackDtoNamed> feedbackDtosNamed = new ArrayList<>();
        for (Feedback feedback : feedbackList) {
            FeedbackDtoNamed feedbackDtoNamed1 = new FeedbackDtoNamed();
            feedbackDtoNamed1.setId(feedback.getId());
            feedbackDtoNamed1.setFeedback(feedback.getFeedback());
            feedbackDtoNamed1.setRating(feedback.getRater());
            feedbackDtoNamed1.setRated(feedback.getRated());
            feedbackDtoNamed1.setCreated(feedback.getCreated());
            feedbackDtoNamed1.setRatedName(userService.findApplicationUserById(feedback.getRated()).getFirstname() + " " + userService.findApplicationUserById(feedback.getRated()).getLastname());
            feedbackDtosNamed.add(feedbackDtoNamed1);
        }
        return feedbackDtosNamed.toArray(new FeedbackDtoNamed[feedbackDtosNamed.size()]);
    }

    @Override
    public FeedbackDto[] getFeedbackByAndForStudent(long id1, long id2) {
        LOGGER.trace("getFeedbackByAndForStudent: {} {}", id1, id2);
        List<Feedback> feedbackList = feedbackRepository.getByRatedAndRater(id1, id2);
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(feedbackList, FeedbackDto[].class);
    }

    @Override
    public boolean chatExists(long studentId1, long studentId2) {
        LOGGER.trace("chatExists: {}, {}", studentId1, studentId2);
        return studentId1 != studentId2 && !chatRoomRepository.findAllBySenderAndReceiverId(studentId1, studentId2).isEmpty();
    }

    @Override
    public void giveFeedback(FeedbackCreateDto feedbackCreateDto, long ratingUserId) throws Exception {
        LOGGER.trace("giveFeedback: {}", feedbackCreateDto);
        if (Objects.equals(ratingUserId, feedbackCreateDto.rated)) {
            throw new ValidationException("Cannot give feedback to yourself");
        }
        if (!chatExists(ratingUserId, feedbackCreateDto.rated)) {
            throw new ValidationException("A chat between the participants must exist");
        }
        if (feedbackCreateDto.feedback.length() < 3) {
            throw new ValidationException("Message too short");
        }
        if (feedbackCreateDto.feedback.length() > 500) {
            throw new ValidationException("Message too long");
        }
        Feedback feedback = new Feedback();
        feedback.setRated(feedbackCreateDto.rated);
        feedback.setRater(ratingUserId);
        feedback.setFeedback(feedbackCreateDto.feedback);
        feedback.setCreated(new Date());
        feedbackRepository.save(feedback);
    }

    @Override
    public void deleteFeedbackOfStudent(long student) {
        LOGGER.trace("deleteFeedbackOfStudent: {}", student);
        feedbackRepository.deleteFeedbackByRater(student);
    }

    @Override
    public void deleteFeedbackByIdAdmin(long id) {
        LOGGER.trace("deleteFeedbackByIdAdmin: {}", id);
        feedbackRepository.deleteFeedbackById(id);
    }

    @Override
    public void deleteFeedbackByIdStudent(long deleteId, long requestUserId) throws Exception {
        LOGGER.trace("deleteFeedbackByIdStudent: {} {}", deleteId, requestUserId);
        Feedback feedback = feedbackRepository.findFeedbackById(deleteId);
        if (feedback.getRated() != requestUserId && feedback.getRater() != requestUserId) {
            throw new ValidationException("You can only delete feedback written or received by you");
        }
        feedbackRepository.deleteFeedbackById(deleteId);
    }

    @Override
    public Feedback getFeedbackByFeedbackId(Long id) {
        LOGGER.trace("getReporterIdByFeedbackId: {}", id);
        return feedbackRepository.findFeedbackById(id);
    }

}
