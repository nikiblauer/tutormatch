package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserBaseInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSubjectsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectsListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserMatchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.SubjectService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserMatchService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserEndpoint {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ApplicationUserMapper mapper;
    private final SubjectService subjectService;

    private final UserMatchService userMatchService;

    public UserEndpoint(UserService userService, ApplicationUserMapper mapper, SubjectService subjectService, UserMatchService userMatchService) {
        this.userService = userService;
        this.mapper = mapper;
        this.subjectService = subjectService;
        this.userMatchService = userMatchService;
    }

    @PermitAll
    @PostMapping
    public ApplicationUserDto create(@RequestBody CreateApplicationUserDto toCreate) throws ValidationException {
        LOGGER.info("POST /api/v1/user/ body: {}", toCreate);
        ApplicationUser user = userService.create(toCreate);
        return mapper.applicationUserToDto(user);
    }

    @GetMapping(value = "/verify/{token}")
    @PermitAll
    public ResponseEntity verifyEmail(@PathVariable("token") String token) {
        LOGGER.info("PUT /api/v1/user/verify/{}", token);
        if (userService.verifyEmail(token)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @Secured("ROLE_USER")
    @PutMapping(value = "/subjects")
    @Operation(summary = "Set subjects for a user", security = @SecurityRequirement(name = "apiKey"))
    public void setUserSubjects(@Valid @RequestBody SubjectsListDto listDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/user/subjects body:{}", listDto);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser student = userService.findApplicationUserByEmail(userEmail);
        subjectService.setUserSubjects(student, listDto.traineeSubjects, listDto.tutorSubjects);
    }



    @Secured("ROLE_USER")
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UpdateApplicationUserDto updateUser(@Valid @RequestBody UpdateApplicationUserDto applicationUserDto) throws Exception {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("PUT /api/v1/user with email: {}, body: {}", userEmail, applicationUserDto);

        var user = userService.updateUser(userEmail, applicationUserDto);
        return mapper.toUpdateDto(user);
    }

    @Secured("ROLE_USER")
    @GetMapping("/matches")
    public Stream<UserMatchDto> getUserMatches() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMatchService.findMatchingsForUser(userEmail);
    }

    @PermitAll
    @GetMapping("{id}")
    public UserBaseInfoDto getUserDetailsById(@PathVariable("id") Long id) {
        ApplicationUser user = userService.findApplicationUserById(id);
        return mapper.mapApplicationUserToApplicationUserDto(user);
    }

    @PermitAll
    @GetMapping("{id}/subjects")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationUserSubjectsDto getUserSubjectsById(@PathVariable("id") Long id) {
        LOGGER.info("GET /api/v1/user/{}/subjects", id);
        ApplicationUser user = userService.findApplicationUserById(id);
        List<UserSubject> subjects = subjectService.findSubjectsByUser(user);
        return mapper.mapUserAndSubjectsToUserSubjectDto(user, subjects);
    }

    @PermitAll
    @GetMapping("subjects")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationUserSubjectsDto getUserSubjectsByEmail() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userService.findApplicationUserByEmail(userEmail);
        List<UserSubject> subjects = subjectService.findSubjectsByUser(user);
        return mapper.mapUserAndSubjectsToUserSubjectDto(user, subjects);
    }
}
