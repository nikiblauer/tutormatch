package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BanReasonDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CoverageSubjectsStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackDtoNamed;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentSubjectInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentSubjectsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectsListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TopStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentAsAdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserBanDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SubjectMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import at.ac.tuwien.sepr.groupphase.backend.exception.SubjectPreviewException;
import at.ac.tuwien.sepr.groupphase.backend.exception.TissClientException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.StatisticService;
import at.ac.tuwien.sepr.groupphase.backend.service.SubjectService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.RatingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/admin")
@Tag(name = "Admin Endpoint")
public class AdminEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    //service
    private final UserService userService;
    private final SubjectService subjectService;
    private final StatisticService statisticService;
    //mapper
    private final ApplicationUserMapper userMapper;
    private final SubjectMapper subjectMapper;
    private final RatingServiceImpl ratingService;


    @Autowired
    public AdminEndpoint(UserService userService, ApplicationUserMapper mapper,
                         SubjectService subjectService, SubjectMapper subjectMapper, StatisticService statisticService, RatingServiceImpl ratingService) {
        this.userService = userService;
        this.userMapper = mapper;
        this.subjectService = subjectService;
        this.subjectMapper = subjectMapper;
        this.statisticService = statisticService;
        this.ratingService = ratingService;
    }

    @Operation(
        description = "This endpoint returns all users, that meet the search criteria for the Name or MatrNumber. The criterias can be empty.",
        summary = "Admin endpoint, get all searched users.")
    @Secured("ROLE_ADMIN")
    @GetMapping("/users")
    public Page<StudentDto> searchUsers(
        @RequestParam(name = "fullname", required = false) String fullname,
        @RequestParam(name = "matrNumber", required = false) Long matrNumber,
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "verified", required = false) Boolean verified,
        Pageable pageable) {
        Boolean hasBan = null;

        if (status != null) {
            String compareStatus = status.toLowerCase().trim();
            if (compareStatus.equals("banned")) {
                hasBan = true;
            }
            if (compareStatus.equals("active")) {
                hasBan = false;
            }
        }

        Page<ApplicationUser> pageOfUsers = userService.queryUsers(fullname, matrNumber, hasBan, pageable, verified);
        return pageOfUsers.map(userMapper::applicationUserToDto);
    }

    @Operation(
        description = "Get all the Details of a student with the ID. This includes also all the subjects of the student which he has chosen",
        summary = "Get all user details.")
    @Secured("ROLE_ADMIN")
    @GetMapping("/users/{id}")
    public StudentSubjectInfoDto getUserDetails(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET /api/v1/admin/users/{}", id);
        ApplicationUser user = userService.findApplicationUserById(id);
        StudentSubjectInfoDto resultingUser = userMapper.applicationUserToSubjectsDto(user);

        //get subjects for user id and set them in the resultingUser
        List<String> tutorSubjects = userService.getUserSubjectsByRole(id, "tutor");
        List<String> traineeSubjects = userService.getUserSubjectsByRole(id, "trainee");

        resultingUser.setTutorSubjects(tutorSubjects.toArray(new String[0]));
        resultingUser.setTraineeSubjects(traineeSubjects.toArray(new String[0]));
        return resultingUser;
    }

    @Operation(
        description = "Admin can update all the user details",
        summary = "Update user")
    @Secured("ROLE_ADMIN")
    @PutMapping("/users/update")
    public UpdateStudentAsAdminDto updateUserDetails(@Valid @RequestBody UpdateStudentAsAdminDto applicationUserDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/admin/users/update with body {}", applicationUserDto);
        Long id = applicationUserDto.id;
        String userEmail = userService.findApplicationUserById(id).getDetails().getEmail();
        var user = userService.updateUserIncludingMatrNr(userEmail, applicationUserDto);
        return userMapper.toAdminUpdateDto(user);
    }

    @Operation(
        description = "Create a ban entry for a user",
        summary = "Ban User")
    @Secured("ROLE_ADMIN")
    @PostMapping("/users/{id}/ban")
    public void banUser(@PathVariable(name = "id") Long id, @RequestBody @Valid BanReasonDto reason) {
        LOGGER.info("POST /users/{}/ban with reason: {}", id, reason);
        userService.banUser(id, reason.getReason());
    }

    @Operation(
        description = "Get info of banned user.",
        summary = "Get Banned User")
    @Secured("ROLE_ADMIN")
    @GetMapping("/users/{id}/ban")
    public UserBanDetailsDto getBanUser(@PathVariable(name = "id") Long id) {
        var ban = userService.getBanForUser(id);
        var user = userService.findApplicationUserById(id);

        return userMapper.mapToUserBanDetailsDto(user, ban);
    }

    @Operation(
        description = "Create a new subject.",
        summary = "Create subject"
    )
    @Secured("ROLE_ADMIN")
    @PostMapping("/subject")
    public SubjectDetailDto createSubject(@Valid @RequestBody SubjectCreateDto subjectDetailDto) throws ValidationException {
        LOGGER.info("POST /api/v1/admin/subject body: {}", subjectDetailDto);
        Subject subject = subjectService.createSubject(subjectDetailDto);
        return subjectMapper.subjectToSubjectDetailDto(subject);
    }

    @Operation(
        description = "Update an already existing subject.",
        summary = "Update subject")
    @Secured("ROLE_ADMIN")
    @PutMapping("/subject")
    public SubjectDetailDto updateSubject(@Valid @RequestBody SubjectDetailDto subjectDetailDto) throws Exception {
        LOGGER.info("PUT /api/v1/admin/subject body: {}", subjectDetailDto);
        Subject subject = subjectService.updateSubject(subjectDetailDto);
        return subjectMapper.subjectToSubjectDetailDto(subject);
    }

    @Operation(
        description = "Remove a subject including all references to that subject",
        summary = "Remove subject")
    @Secured("ROLE_ADMIN")
    @DeleteMapping("{id}")
    public void removeSubject(@PathVariable("id") Long id) {
        LOGGER.info("DELETE /api/v1/admin/{}", id);
        subjectService.deleteSubject(id);
    }

    @Operation(
        description = "Get all subjects a user has selected ",
        summary = "Get subjects by user id")
    @Secured("ROLE_ADMIN")
    @GetMapping("/users/subjects/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StudentSubjectsDto getUserSubjectsById(@PathVariable("id") Long id) {
        LOGGER.info("GET /api/v1/user/{}/subjects", id);
        ApplicationUser user = userService.findApplicationUserById(id);
        List<UserSubject> subjects = subjectService.findSubjectsByUser(user);
        return userMapper.mapUserAndSubjectsToUserSubjectDto(user, subjects);
    }

    @Operation(
        description = "Update the subjects a user has selected. User is identified by Id",
        summary = "Update user subjects by user id")
    @Secured("ROLE_ADMIN")
    @PutMapping("/users/subjects/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserSubjectsById(@PathVariable("id") Long id, @Valid @RequestBody SubjectsListDto listDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/user/subjects body:{}", listDto);
        ApplicationUser student = userService.findApplicationUserById(id);
        subjectService.setUserSubjects(student, listDto.traineeSubjects, listDto.tutorSubjects);
    }

    @Operation(
        description = "Retrieves a preview of the subject for the given course number and semester.",
        summary = "Get subject preview by course number and semester"
    )
    @Secured("ROLE_ADMIN")
    @GetMapping("/subject/courses/{courseNr}/semesters/{semester}/preview")
    @ResponseStatus(HttpStatus.OK)
    public SubjectCreateDto getSubjectPreview(@PathVariable("courseNr") String courseNr, @PathVariable("semester") String semester) {
        try {
            var preview = subjectService.createSubjectPreviewFromTiss(courseNr, semester);
            return subjectMapper.subjectToSubjectDetailDto(preview);
        } catch (TissClientException e) {
            LOGGER.error("Error loading subject preview from TISS for course number: {} and semester: {}", courseNr, semester, e);
            throw new SubjectPreviewException("Could not load subject from Tiss. Reason: " + e.getMessage(), e);
        }
    }

    @Operation(
        description = "Gets a simple statistic of Subjects which is defined in the DTO SimpleStatisticsDto.",
        summary = "Get statistics from backend.")
    @Secured("ROLE_ADMIN")
    @GetMapping("/statistics/simple")
    public SimpleStatisticsDto getSimpleStatistics() {
        LOGGER.info("GET /api/v1/admin/statistics/simple");
        return statisticService.getSimpleStatistics();
    }

    @Operation(
        description = "Gets a extended list of statistics, like how many subjects are needed and offered currently.",
        summary = "Get top Statistics from backend.")
    @Secured("ROLE_ADMIN")
    @GetMapping("/statistics/extended")
    public TopStatisticsDto getExtendedStatisticsList(@RequestParam(name = "x") int x) {
        LOGGER.info("GET /api/v1/admin/statistics/extended");
        return statisticService.getExtendedStatistics(x);
    }

    @Operation(
        description = "Get the feedback for a user.",
        summary = "Get received feedback.")
    @Secured("ROLE_ADMIN")
    @GetMapping("/feedback/in/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FeedbackCreateDto[] getFeedbackOfUser(@PathVariable("id") Long id) {
        LOGGER.info("PUT /api/v1/feedback/in/{}", id);
        return ratingService.getFeedbackOfStudent(id);
    }

    @Operation(
        description = "Get the feedback by a user.",
        summary = "Get posted feedback.")
    @Secured("ROLE_ADMIN")
    @GetMapping("/feedback/out/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FeedbackDtoNamed[] getFeedbackByUser(@PathVariable("id") Long id) {
        LOGGER.info("PUT /api/v1/feedback/out/{}", id);
        return ratingService.getFeedbackByStudent(id);
    }

    @Operation(
        description = "Delete the feedback by id.",
        summary = "Delete feedback.")
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/feedback/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFeedbackById(@PathVariable("id") Long id) {
        LOGGER.info("PUT /api/v1/feedback/delete/{}", id);
        ratingService.deleteFeedbackByIdAdmin(id);
    }

    @Operation(description = "Get the statistics of which subjects have a lot of trainees but no coverage which means no one is offering this subjects.",
        summary = "Get coverage subjects statistics"
    )
    @Secured("ROLE_ADMIN")
    @GetMapping("/statistics/coverage")
    public CoverageSubjectsStatisticsDto getCoverageSubjectsStatistics(@RequestParam(name = "x", defaultValue = "5") int x) {
        return statisticService.getCoverageSubjectsStatistics(x);
    }
}
