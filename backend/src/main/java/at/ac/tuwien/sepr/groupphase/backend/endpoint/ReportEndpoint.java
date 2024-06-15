package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.RatingService;
import at.ac.tuwien.sepr.groupphase.backend.service.ReportService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/report")
@Tag(name = "Report Endpoint")
public class ReportEndpoint {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RatingService ratingService;
    private final ReportService reportService;

    public ReportEndpoint(UserService userService, RatingService ratingService, ReportService reportService) {
        this.userService = userService;
        this.ratingService = ratingService;
        this.reportService = reportService;
    }

    @Operation(
        summary = "report a user",
        description = "In this put request the user can report a user to the admin"
    )
    @Secured("ROLE_USER")
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void reportUser(@PathVariable(name = "id") Long id, @RequestBody String reason) throws ValidationException {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser reporter = userService.findApplicationUserByEmail(userEmail);
        ApplicationUser reportedUser = userService.findApplicationUserById(id);
        this.reportService.reportUser(reporter, reportedUser, reason);
    }
    @Operation(
        summary = "report a user because of a feedback",
        description = "In this put request the user can report a user to the admin because of a feedback"
    )
    @Secured("ROLE_USER")
    @PostMapping("/feedback/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void reportUserFeedback(@PathVariable(name = "id") Long feedback, @RequestBody String reason) throws ValidationException {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser reporter = userService.findApplicationUserByEmail(userEmail);
        Feedback feedback1 = ratingService.getFeedbackByFeedbackId(feedback);
        ApplicationUser reportedUser = userService.findApplicationUserById(feedback1.getId());
        this.reportService.reportUserFeedback(reporter, reportedUser, reason, feedback1);
    }
    @Operation(
        summary = "report a user because of a chat",
        description = "In this put request the user can report a user to the admin because of a chat"
    )
    @Secured("ROLE_USER")
    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.CREATED)
    public void reportUserChat(@RequestBody ChatRoomDto chatRoom, @RequestBody String reason) {
        LOGGER.info("POST /api/v1/report/chat: {}", chatRoom);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser student = userService.findApplicationUserByEmail(userEmail);
        //this.reportService.reportUserChat();
    }

}
