package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserEndpoint {
    private final UserService userService;
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ApplicationUserMapper mapper;


    public UserEndpoint(UserService userService, ApplicationUserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @PermitAll
    @PostMapping
    public ApplicationUserDto create(@RequestBody ApplicationUserDto applicationUserDto) throws Exception {
        ApplicationUser user = userService.create(applicationUserDto);
        return mapper.mapUserToDto(user, user.getDetails());
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    public ApplicationUserDto updateUser(@PathVariable("id") Long id, @Valid @RequestBody ApplicationUserDto applicationUserDto) throws Exception {
        LOG.info("Updating user with id: {}", id);
        return userService.updateUser(id, applicationUserDto);
    }
}
