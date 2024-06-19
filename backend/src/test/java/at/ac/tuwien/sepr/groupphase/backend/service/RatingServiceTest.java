package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.FeedbackRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ActiveProfiles({"test", "generateData"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RatingServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private RatingServiceImpl ratingService;

    @Test
    public void getFeedbackOfFirstStudentReturnsNoFeedback() throws Exception {
        FeedbackDto[] feedbackDto = ratingService.getFeedbackOfStudent(1);
        assertEquals(0, feedbackDto.length);
    }

    @Test
    public void getFeedbackByFirstStudentReturnsOneFeedback() throws Exception {
        FeedbackDto[] feedbackDto = ratingService.getFeedbackByStudent(1);
        assertEquals(1, feedbackDto.length);
    }

    @Test
    public void getFeedbackOfLastStudentReturnsOneFeedback() throws Exception {
        FeedbackDto[] feedbackDto = ratingService.getFeedbackOfStudent(9);
        assertEquals(1, feedbackDto.length);
    }
    @Test
    public void getFeedbackByLastStudentReturnsNoFeedback() throws Exception {
        FeedbackDto[] feedbackDto = ratingService.getFeedbackByStudent(9);
        assertEquals(0, feedbackDto.length);
    }

    @Test
    public void getFeedbackFromFirstToSecondUserReturnsFeedback() throws Exception {
        FeedbackDto[] feedbackDto = ratingService.getFeedbackByAndForStudent(2,1);
        assertEquals(1, feedbackDto.length);
    }

    @Test
    public void chatExistsReturnsTrueForFirstToSecondUser() throws Exception {
        assertTrue(ratingService.chatExists(2,1));
    }

    @Test
    public void chatExistsReturnsFalseForFirstToThirdUser() throws Exception {
        assertFalse(ratingService.chatExists(1,3));
    }

    @Test
    public void giveFeedbackSuccessfulWithValidInput() throws Exception {
        FeedbackCreateDto feedbackCreateDto = new FeedbackCreateDto();
        feedbackCreateDto.setFeedback("test123");
        feedbackCreateDto.setRated(1L);
        ratingService.giveFeedback(feedbackCreateDto, 2L);
        List<Feedback> feedback = feedbackRepository.getByRatedAndRater(1,2);
        assertEquals(feedback.size(),1);
        assertEquals(feedback.getFirst().getFeedback(),"test123");
    }

    @Test
    public void giveFeedbackThrowsExceptionWhenRaterAndRatedIdAreEqual() throws Exception {
        FeedbackCreateDto feedbackCreateDto = new FeedbackCreateDto();
        feedbackCreateDto.setFeedback("test123");
        feedbackCreateDto.setRated(1L);
        assertThatThrownBy(() -> {
            ratingService.giveFeedback(feedbackCreateDto, 1L);
        }).isInstanceOf(ValidationException.class).hasMessageContaining("Cannot give feedback to yourself");
    }

    @Test
    public void giveFeedbackThrowsExceptionWhenNoChatExists() throws Exception {
        FeedbackCreateDto feedbackCreateDto = new FeedbackCreateDto();
        feedbackCreateDto.setFeedback("test123");
        feedbackCreateDto.setRated(1L);
        assertThatThrownBy(() -> {
            ratingService.giveFeedback(feedbackCreateDto, 8L);
        }).isInstanceOf(ValidationException.class).hasMessageContaining("A chat between the participants must exist");
    }

    @Test
    public void giveFeedbackThrowsExceptionWhenMoreThan500Words() throws Exception {
        FeedbackCreateDto feedbackCreateDto = new FeedbackCreateDto();
        feedbackCreateDto.setFeedback(new String(new char[501]).replace('\0', '\n'));
        feedbackCreateDto.setRated(1L);
        assertThatThrownBy(() -> {
            ratingService.giveFeedback(feedbackCreateDto, 2L);
        }).isInstanceOf(ValidationException.class).hasMessageContaining("Message too long");
    }


    @Test
    public void deleteFeedbackByIdStudentSuccessfulWhenRequestUserIdIsRatedId() throws Exception {
        Feedback feedback = feedbackRepository.findFeedbackById(1L);
        assertNotNull(feedback);
        ratingService.deleteFeedbackByIdStudent(1L,feedback.getRated());
        Feedback feedbackAfter = feedbackRepository.findFeedbackById(1L);
        assertNull(feedbackAfter);
    }
    @Test
    public void deleteFeedbackByIdStudentSuccessfulWhenRequestUserIdIsRaterId() throws Exception {
        Feedback feedback = feedbackRepository.findFeedbackById(1L);
        assertNotNull(feedback);
        ratingService.deleteFeedbackByIdStudent(1L,feedback.getRater());
        Feedback feedbackAfter = feedbackRepository.findFeedbackById(1L);
        assertNull(feedbackAfter);
    }
    @Test
    public void deleteFeedbackByIdStudentThrowsExceptionWhenRequestUserIdIsNotRatedIdOrRaterId() throws Exception {
        Feedback feedback = feedbackRepository.findFeedbackById(1L);
        assertNotNull(feedback);
        long idNotInRatedOrRater = Math.max(feedback.getRated(), feedback.getRater()) + 1;
        assertThatThrownBy(() -> {
            ratingService.deleteFeedbackByIdStudent(1L, idNotInRatedOrRater);
        }).isInstanceOf(ValidationException.class).hasMessageContaining("You can only delete feedback written or received by you");

    }
    @Test
    public void deleteFeedbackByIdAdminSuccessful() throws Exception {
        Feedback feedback = feedbackRepository.findFeedbackById(1L);
        assertNotNull(feedback);
        ratingService.deleteFeedbackByIdAdmin(1L);
        Feedback feedbackAfter = feedbackRepository.findFeedbackById(1L);
        assertNull(feedbackAfter);
    }
}
