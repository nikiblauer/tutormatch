package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailsWithSubjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SubjectMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.SubjectService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/admin")
public class AdminEndpoint {

    private final UserService userService;
    private final ApplicationUserMapper userMapper;
    private final SubjectService subjectService;
    private final SubjectMapper subjectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Autowired
    public AdminEndpoint(UserService userService, ApplicationUserMapper mapper, SubjectService subjectService, SubjectMapper subjectMapper) {
        this.userService = userService;
        this.userMapper = mapper;
        this.subjectService = subjectService;
        this.subjectMapper = subjectMapper;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users")
    public Page<ApplicationUserDto> searchUsers(
        @RequestParam(name = "fullname", required = false) String fullname,
        @RequestParam(name = "matrNumber", required = false) Long matrNumber,
        Pageable pageable) {
        Page<ApplicationUser> pageOfUsers = userService.queryUsers(fullname, matrNumber, pageable);
        return pageOfUsers.map(userMapper::applicationUserToDto);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users/{id}")
    public UserDetailsWithSubjectDto getUserDetails(@PathVariable(name = "id") Long id) {
        //get user by id and map to UserDetailsWithSubjectDto
        ApplicationUser user = userService.findApplicationUserById(id);
        UserDetailsWithSubjectDto resultingUser = userMapper.applicationUserToSubjectsDto(user);

        //get subjects for user id and set them in the resultingUser
        List<String> tutorSubjects = userService.getUserSubjectsByRole(id, "tutor");
        List<String> traineeSubjects = userService.getUserSubjectsByRole(id, "trainee");

        resultingUser.setTutorSubjects(tutorSubjects.toArray(new String[0]));
        resultingUser.setTraineeSubjects(traineeSubjects.toArray(new String[0]));
        return resultingUser;
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
}
