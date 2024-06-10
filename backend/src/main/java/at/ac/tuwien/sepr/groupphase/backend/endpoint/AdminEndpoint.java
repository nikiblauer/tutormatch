package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BanReasonDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentSubjectInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentSubjectsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectsListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentAsAdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TopStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserBanDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SubjectMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.StatisticService;
import at.ac.tuwien.sepr.groupphase.backend.service.SubjectService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
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
public class AdminEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    //service
    private final UserService userService;
    private final SubjectService subjectService;
    private final StatisticService statisticService;
    //mapper
    private final ApplicationUserMapper userMapper;
    private final SubjectMapper subjectMapper;

    @Autowired
    public AdminEndpoint(UserService userService, ApplicationUserMapper mapper,
                         SubjectService subjectService, SubjectMapper subjectMapper, StatisticService statisticService) {
        this.userService = userService;
        this.userMapper = mapper;
        this.subjectService = subjectService;
        this.subjectMapper = subjectMapper;
        this.statisticService = statisticService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users")
    public Page<StudentDto> searchUsers(
        @RequestParam(name = "fullname", required = false) String fullname,
        @RequestParam(name = "matrNumber", required = false) Long matrNumber,
        @RequestParam(name = "status", required = false) String status,
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

        Page<ApplicationUser> pageOfUsers = userService.queryUsers(fullname, matrNumber, hasBan, pageable);
        return pageOfUsers.map(userMapper::applicationUserToDto);
    }

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

    @Secured("ROLE_ADMIN")
    @PutMapping("/users/update")
    public UpdateStudentAsAdminDto updateUserDetails(@Valid @RequestBody UpdateStudentAsAdminDto applicationUserDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/admin/users/update with body {}", applicationUserDto);
        Long id = applicationUserDto.id;
        String userEmail = userService.findApplicationUserById(id).getDetails().getEmail();
        var user = userService.updateUserIncludingMatrNr(userEmail, applicationUserDto);
        return userMapper.toAdminUpdateDto(user);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/users/{id}/ban")
    public void banUser(@PathVariable(name = "id") Long id, @RequestBody @Valid BanReasonDto reason) throws ValidationException {
        LOGGER.info("POST /users/{}/ban with reason: {}", id, reason);
        userService.banUser(id, reason.getReason());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users/{id}/ban")
    public UserBanDetailsDto getBanUser(@PathVariable(name = "id") Long id) throws ValidationException {
        var ban = userService.getBanForUser(id);
        var user = userService.findApplicationUserById(id);

        return userMapper.mapToUserBanDetailsDto(user, ban);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/subject")
    public SubjectDetailDto createSubject(@Valid @RequestBody SubjectCreateDto subjectDetailDto) throws ValidationException {
        LOGGER.info("POST /api/v1/admin/subject body: {}", subjectDetailDto);
        Subject subject = subjectService.createSubject(subjectDetailDto);
        return subjectMapper.subjectToSubjectDetailDto(subject);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/subject")
    public SubjectDetailDto updateSubject(@Valid @RequestBody SubjectDetailDto subjectDetailDto) throws Exception {
        LOGGER.info("PUT /api/v1/admin/subject body: {}", subjectDetailDto);
        Subject subject = subjectService.updateSubject(subjectDetailDto);
        return subjectMapper.subjectToSubjectDetailDto(subject);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("{id}")
    public void removeSubject(@PathVariable("id") Long id) {
        LOGGER.info("DELETE /api/v1/admin/{}", id);
        subjectService.deleteSubject(id);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users/subjects/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StudentSubjectsDto getUserSubjectsById(@PathVariable("id") Long id) {
        LOGGER.info("GET /api/v1/user/{}/subjects", id);
        ApplicationUser user = userService.findApplicationUserById(id);
        List<UserSubject> subjects = subjectService.findSubjectsByUser(user);
        return userMapper.mapUserAndSubjectsToUserSubjectDto(user, subjects);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/users/subjects/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserSubjectsById(@PathVariable("id") Long id, @Valid @RequestBody SubjectsListDto listDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/user/subjects body:{}", listDto);
        ApplicationUser student = userService.findApplicationUserById(id);
        subjectService.setUserSubjects(student, listDto.traineeSubjects, listDto.tutorSubjects);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/statistics/simple")
    public SimpleStatisticsDto getSimpleStatistics() {
        LOGGER.info("GET /api/v1/admin/statistics/simple");
        return statisticService.getSimpleStatistics();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/statistics/extended")
    public TopStatisticsDto getExtendedStatisticsList(@RequestParam(name = "x") int x) {
        LOGGER.info("GET /api/v1/admin/statistics/extended");
        return statisticService.getExtendedStatistics(x);
    }
}
