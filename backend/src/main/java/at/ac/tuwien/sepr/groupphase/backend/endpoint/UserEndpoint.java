package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectsListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserMatchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
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
import org.springframework.security.access.annotation.Secured;
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
    public ApplicationUserDto create(@RequestBody ApplicationUserDto applicationUserDto) throws ValidationException {
        ApplicationUser user = userService.create(applicationUserDto);
        return mapper.mapUserToDto(user, user.getDetails());
    }

    @Secured("ROLE_USER")
    @PutMapping(value = "/{id}/subjects")
    @Operation(summary = "Set subjects for a user", security = @SecurityRequirement(name = "apiKey"))
    public void setUserSubjects(@PathVariable(name = "id") Long id, @Valid @RequestBody SubjectsListDto listDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/user/{}/subjects body:{}", id, listDto);
        ApplicationUser student = userService.findApplicationUserById(id);
        subjectService.setUserSubjects(student, listDto.traineeSubjects, listDto.tutorSubjects);
        //return mapper.mapUserToDto(student, student.getDetails());
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    public ApplicationUserDto updateUser(@PathVariable("id") Long id, @Valid @RequestBody ApplicationUserDto applicationUserDto) throws Exception {
        ApplicationUser user = userService.updateUser(id, applicationUserDto);
        return mapper.mapUserToDto(user, user.getDetails());
    }

    @PermitAll
    @GetMapping("{id}/matches")
    public Stream<UserMatchDto> getUserMatches(@PathVariable("id") Long id) {
        return userMatchService.findMatchingUserByUserIdAsStream(id);
    }
}
