package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswordResetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.UnverifiedAccountException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.email.EmailSmtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserValidator validator;
    private final EmailSmtpService emailService;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer,
                                   UserValidator validator, EmailSmtpService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.validator = validator;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return new User(applicationUser.getDetails().getEmail(), applicationUser.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) throws NotFoundException {
        LOGGER.debug("Find application user by email");
        ApplicationUser applicationUser = userRepository.findApplicationUserByDetails_Email(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException("No user found with this email");
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        LOGGER.trace("Login as User: {}", userLoginDto);
        UserDetails userDetails;
        ApplicationUser applicationUser;
        try {
            userDetails = loadUserByUsername(userLoginDto.getEmail());
            applicationUser = findApplicationUserByEmail(userLoginDto.getEmail());
        } catch (NotFoundException e) {
            throw new NotFoundException("No user found with this email");
        }
        if (!applicationUser.getVerified()) {
            throw new UnverifiedAccountException("Account is not verified yet. Please verify your account to log in.");
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
    public ApplicationUser create(CreateApplicationUserDto toCreate) throws ValidationException {
        LOGGER.trace("Create user by applicationUserDto: {}", toCreate);
        validator.validateForCreate(toCreate);
        if (!userRepository.findAllByDetails_Email(toCreate.email).isEmpty()) {
            throw new ValidationException("Email already exits please try an other one");
        }
        ContactDetails details = new ContactDetails("", toCreate.email, new Address("", 0, ""));

        String encodedPassword = passwordEncoder.encode(toCreate.password);

        ApplicationUser applicationUser = new ApplicationUser(
            encodedPassword,
            false,
            toCreate.firstname.trim().replaceAll("\\s+", " "),
            toCreate.lastname.trim().replaceAll("\\s+", " "),
            toCreate.matrNumber,
            details,
            false);
        emailService.sendVerificationEmail(toCreate);
        return userRepository.save(applicationUser);
    }

    @Override
    public ApplicationUser findApplicationUserById(Long id) {
        LOGGER.trace("Find application user by id:{}", id);
        ApplicationUser applicationUser = userRepository.findById(id).orElseThrow(() ->
            new NotFoundException(String.format("Could not find the user with the id %s", id)));
        if (applicationUser != null && !applicationUser.getAdmin()) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the id %s", id));
    }

    @Override
    public List<String> getUserSubjectsByRole(Long id, String role) {
        LOGGER.trace("Find subjects by user id:{}", id);
        return userRepository.getUserSubjectsByRole(id, role);
    }

    @Override
    public boolean verifyEmail(String token) {
        LOGGER.trace("Verify Email with token:{}", token);
        String tokenEmail = jwtTokenizer.extractUsernameFromVerificationToken(token);
        try {
            UserDetails userDetails = loadUserByUsername(tokenEmail);
            if (userDetails != null
                && userDetails.isAccountNonExpired()
                && userDetails.isAccountNonLocked()
                && userDetails.isCredentialsNonExpired()
            ) {
                ApplicationUser applicationUser = findApplicationUserByEmail(tokenEmail);
                applicationUser.setVerified(true);
                userRepository.save(applicationUser);
            } else {
                return false;
            }
        } catch (UsernameNotFoundException | NotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public void requestPasswordReset(String email) {
        LOGGER.trace("Send Password Reset Email to :{}", email);
        try {
            UserDetails userDetails = loadUserByUsername(email);
            if (userDetails != null
                    && userDetails.isAccountNonExpired()
                    && userDetails.isAccountNonLocked()
                    && userDetails.isCredentialsNonExpired()
            ) {
                ApplicationUser applicationUser = findApplicationUserByEmail(email);
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
            UserDetails userDetails = loadUserByUsername(tokenEmail);
            if (userDetails != null
                    && userDetails.isAccountNonExpired()
                    && userDetails.isAccountNonLocked()
                    && userDetails.isCredentialsNonExpired()
            ) {
                ApplicationUser applicationUser = findApplicationUserByEmail(tokenEmail);
                validator.validatePasswordChange(resetDto);
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

    @Override
    public ApplicationUser updateUser(String userEmail, UpdateApplicationUserDto applicationUserDto) throws ValidationException {
        LOGGER.trace("Updating user with email: {}", userEmail);
        //remove whitespaces from telNr
        applicationUserDto.telNr = applicationUserDto.telNr != null ? applicationUserDto.telNr.replace(" ", "") : null;
        validator.verifyUserData(applicationUserDto);

        ApplicationUser applicationUser = userRepository.findApplicationUserByDetails_Email(userEmail);
        if (applicationUser == null) {
            throw new NotFoundException(String.format("User with email %s not found", userEmail));
        }

        applicationUser.setFirstname(applicationUserDto.firstname);
        applicationUser.setLastname(applicationUserDto.lastname);
        applicationUser.getDetails().setTelNr(applicationUserDto.telNr);
        applicationUser.getDetails().getAddress().setStreet(applicationUserDto.street);
        applicationUser.getDetails().getAddress().setAreaCode(applicationUserDto.areaCode);
        applicationUser.getDetails().getAddress().setCity(applicationUserDto.city);

        // Save the updated ApplicationUser in the database
        return userRepository.save(applicationUser);
    }

    @Override
    public Page<ApplicationUser> queryUsers(String fullname, Long matrNumber, Pageable pageable) {
        LOGGER.trace("Getting all users");
        return userRepository.findAllByFullnameOrMatrNumber(fullname, matrNumber, pageable);
    }
}
