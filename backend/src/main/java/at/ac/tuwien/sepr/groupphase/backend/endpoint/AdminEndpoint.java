package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/admin")
public class AdminEndpoint {

    private final UserService userService;
    private final ApplicationUserMapper mapper;


    @Autowired
    public AdminEndpoint(UserService userService, ApplicationUserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users")
    public List<ApplicationUserDto> searchUsers(
        @RequestParam(name = "fullname", required = false) String fullname,
        @RequestParam(name = "matrNumber", required = false) Long matrNumber) {

        List<ApplicationUser> listOfUsers = userService.queryUsers(fullname, matrNumber);
        return listOfUsers.stream()
            .map(user -> mapper.mapUserToDto(user))
            .collect(Collectors.toList());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users/{id}")
    public ApplicationUserDto getUserDetails(@PathVariable(name = "id") Long id) {
        ApplicationUser user = userService.findApplicationUserById(id);
        return mapper.mapUserToDto(user);
    }
}
