package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswordResetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.BannedUserException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.UnverifiedAccountException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.LoginService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.UserValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.email.EmailSmtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserValidator validator;
    private final EmailSmtpService emailService;

    @Autowired
    public LoginServiceImpl(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer,
                            UserValidator validator, EmailSmtpService emailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.validator = validator;
        this.emailService = emailService;
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        LOGGER.trace("Login as User: {}", userLoginDto);
        UserDetails userDetails;
        ApplicationUser applicationUser;
        try {
            userDetails = userService.loadUserByUsername(userLoginDto.getEmail());
            applicationUser = userService.findApplicationUserByEmail(userLoginDto.getEmail());
        } catch (NotFoundException e) {
            throw new NotFoundException("No user found with this email");
        }
        if (!applicationUser.getVerified()) {
            userService.resendVerificationEmail(userLoginDto.getEmail());
            throw new UnverifiedAccountException("Account is not verified yet. Please verify your account to log in.");
        }
        if (applicationUser.isBanned()) {
            throw new BannedUserException("This user account is blocked!");
        }
        if (userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
            && applicationUser.getVerified()
        ) {
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }
        throw new BadCredentialsException("Username or password is incorrect");
    }

    @Override
    public void requestPasswordReset(String email) {
        LOGGER.trace("Send Password Reset Email to :{}", email);
        try {
            UserDetails userDetails = userService.loadUserByUsername(email);
            if (userDetails != null
                && userDetails.isAccountNonExpired()
                && userDetails.isAccountNonLocked()
                && userDetails.isCredentialsNonExpired()
            ) {
                ApplicationUser applicationUser = userService.findApplicationUserByEmail(email);
                emailService.sendPasswordResetEmail(applicationUser);
            }
        } catch (UsernameNotFoundException | NotFoundException ignored) {
            LOGGER.warn("Password Reset Email Request with non-existent User: {}", email);
        }
    }

    @Override
    public boolean changePasswordWithToken(String token, PasswordResetDto resetDto) throws ValidationException {
        LOGGER.trace("Change Password using token :{}", token);
        String tokenEmail = jwtTokenizer.extractUsernameFromVerificationToken(token);
        try {
            UserDetails userDetails = userService.loadUserByUsername(tokenEmail);
            if (userDetails != null
                && userDetails.isAccountNonExpired()
                && userDetails.isAccountNonLocked()
                && userDetails.isCredentialsNonExpired()
            ) {
                ApplicationUser applicationUser = userService.findApplicationUserByEmail(tokenEmail);
                validator.validatePasswordChange(resetDto, applicationUser.getPassword());
                String encodedPassword = passwordEncoder.encode(resetDto.password);
                applicationUser.setPassword(encodedPassword);
                userRepository.save(applicationUser);
                return true;
            }
        } catch (UsernameNotFoundException | NotFoundException ignored) {
            LOGGER.warn("Password Reset submitted with invalid token: {}", token);
        }
        return false;
    }
}
