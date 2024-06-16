package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReportChatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReportDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ReportMapper;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/report")
@Tag(name = "Report Endpoint")
public class ReportEndpoint {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RatingService ratingService;
    private final ReportService reportService;

    private final ReportMapper reportMapper;

    public ReportEndpoint(UserService userService, RatingService ratingService, ReportService reportService, ReportMapper reportMapper) {
        this.userService = userService;
        this.ratingService = ratingService;
        this.reportService = reportService;
        this.reportMapper = reportMapper;
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
    public void reportUserChat(@RequestBody ReportChatDto chatRoom) throws ValidationException {
        LOGGER.info("POST /api/v1/report/chat: {}", chatRoom);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser student = userService.findApplicationUserByEmail(userEmail);
        this.reportService.reportUserChat(chatRoom.getChatId(), student, chatRoom.getReason());
    }

    @Operation(
        summary = "returns all reports",
        description = "a list of all reports with all informations"
    )
    @Secured("ROLE_ADMIN")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<ReportDto> getAllReports() {
        var reports = reportService.getAllReports();
        return this.reportMapper.reportToReportDto(reports);
    }

    @Operation(
        summary = "deletes a report",
        description = "deletes a report"
    )
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteReport(@PathVariable(name = "id") Long id) {
        this.reportService.deleteReport(id);
    }
}
