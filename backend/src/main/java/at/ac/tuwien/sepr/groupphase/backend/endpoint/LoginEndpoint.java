package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/authentication")
@Tag(name = "Login Endpoint")
public class LoginEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LoginService loginService;

    public LoginEndpoint(LoginService loginService) {
        this.loginService = loginService;
    }

    @Operation(
        description = "Login user, returns a corresponding bearer token.",
        summary = "Login User")
    @PermitAll
    @PostMapping
    public String login(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        String origin = request.getHeader(HttpHeaders.ORIGIN);
        LOGGER.info("POST /api/v1/authentication, user = {}, origin = {}", userLoginDto.getEmail(), origin);
        return loginService.login(userLoginDto, origin);
    }
}
