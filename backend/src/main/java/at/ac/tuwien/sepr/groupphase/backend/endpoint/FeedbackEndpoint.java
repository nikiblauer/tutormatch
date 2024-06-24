package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackDtoNamed;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.RatingService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/feedback")
@Tag(name = "Feedback Endpoint")
public class FeedbackEndpoint {
    private final RatingService ratingService;
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public FeedbackEndpoint(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
    }

    @Operation(
        description = "Add feedback to a user. Both participants must have an active chat.",
        summary = "Add feedback.")
    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void addFeedback(@RequestBody FeedbackCreateDto feedbackCreateDto) throws Exception {
        LOGGER.info("POST /api/v1/feedback {}", feedbackCreateDto);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long ratingUserid = userService.findApplicationUserByEmail(userEmail).getId();
        ratingService.giveFeedback(feedbackCreateDto, ratingUserid);
    }

    @Operation(
        description = "Get feedback the request user gave to user with id.",
        summary = "Get feedback.")
    @Secured("ROLE_USER")
    @GetMapping("/get/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FeedbackDto[] getFeedbackByAndFor(@PathVariable("id") Long id) throws Exception {
        LOGGER.info("GET /api/v1/feedback/{}", id);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long ratingUserid = userService.findApplicationUserByEmail(userEmail).getId();
        return ratingService.getFeedbackByAndForStudent(id, ratingUserid);
    }

    @Operation(
        description = "Get the feedback for the request user.",
        summary = "Get feedback for self.")
    @Secured("ROLE_USER")
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public FeedbackDto[] getFeedbackOfSelf() {
        LOGGER.info("GET /api/v1/feedback/me");
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long id = userService.findApplicationUserByEmail(userEmail).getId();
        return ratingService.getFeedbackOfStudent(id);
    }

    @Operation(
        description = "Get the feedback by the request user.",
        summary = "Get feedback by self.")
    @Secured("ROLE_USER")
    @GetMapping("/me/sent")
    @ResponseStatus(HttpStatus.OK)
    public FeedbackDtoNamed[] getFeedbackBySelf() {
        LOGGER.info("GET /api/v1/feedback/me/sent");
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long id = userService.findApplicationUserByEmail(userEmail).getId();
        return ratingService.getFeedbackByStudent(id);
    }

    @Operation(
        description = "Get information if a chat with this user exists.",
        summary = "Get chat information.")
    @Secured("ROLE_USER")
    @GetMapping("/valid/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean getChatExists(@PathVariable("id") Long id1) {
        LOGGER.info("GET /api/v1/feedback/valid/{}", id1);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long id2 = userService.findApplicationUserByEmail(userEmail).getId();
        return ratingService.chatExists(id1, id2);

    }

    @Operation(
        description = "Delete the feedback by id.",
        summary = "Delete feedback.")
    @Secured("ROLE_USER")
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFeedbackById(@PathVariable("id") Long id) throws Exception {
        LOGGER.info("PUT /api/v1/feedback/out/{}", id);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long id2 = userService.findApplicationUserByEmail(userEmail).getId();
        ratingService.deleteFeedbackByIdStudent(id, id2);
    }

}
