package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentAsAdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.email.EmailSmtpService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
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
    public ApplicationUser create(CreateStudentDto toCreate) throws ValidationException {
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
    public void resendVerificationEmail(String email) {
        LOGGER.trace("Resend verification email :{}", email);
        try {
            UserDetails userDetails = loadUserByUsername(email);
            if (userDetails != null
                && userDetails.isAccountNonExpired()
                && userDetails.isAccountNonLocked()
                && userDetails.isCredentialsNonExpired()
            ) {
                ApplicationUser applicationUser = findApplicationUserByEmail(email);
                if (!applicationUser.getVerified()) {
                    CreateStudentDto studentDto = new CreateStudentDto();
                    studentDto.setEmail(email);
                    studentDto.setFirstname(applicationUser.getFirstname());
                    studentDto.setLastname(applicationUser.getLastname());
                    emailService.sendVerificationEmail(studentDto);
                }
            }
        } catch (UsernameNotFoundException | NotFoundException e) {
            throw new NotFoundException(String.format("User with email %s not found", email));
        }
    }

    @Override
    public boolean getVisibility(ApplicationUser user) {
        LOGGER.trace("getVisibility: {}", user);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user.getVisible();
    }

    @Override
    public void updateVisibility(boolean flag, ApplicationUser user) {
        LOGGER.trace("updateVisibility: {},{}", flag, user);
        if (user != null) {
            user.setVisible(flag);
            this.userRepository.save(user);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public ApplicationUser updateUser(String userEmail, UpdateStudentDto applicationUserDto) throws ValidationException {
        LOGGER.trace("Updating user with email: {}", userEmail);
        ApplicationUser applicationUser = buildUser(userEmail, applicationUserDto);
        // Save the updated ApplicationUser in the database
        return userRepository.save(applicationUser);
    }

    @Override
    public ApplicationUser updateUserIncludingMatrNr(String userEmail, UpdateStudentAsAdminDto applicationUserDto) throws ValidationException {
        LOGGER.trace("Updating user with email: {}", userEmail);
        ApplicationUser applicationUser = updateUser(userEmail, applicationUserDto);
        applicationUser.setMatrNumber(applicationUserDto.matrNumber);
        // Save the updated ApplicationUser in the database
        return userRepository.save(applicationUser);
    }

    private ApplicationUser buildUser(String userEmail, UpdateStudentDto user) throws ValidationException {
        LOGGER.trace("buildUser: {},{}", userEmail, user);

        user.telNr = user.telNr.replaceAll(" ", "");
        validator.verifyUserData(user);

        ApplicationUser applicationUser = userRepository.findApplicationUserByDetails_Email(userEmail);
        if (applicationUser == null) {
            throw new NotFoundException(String.format("User with email %s not found", userEmail));
        }

        applicationUser.setFirstname(user.firstname);
        applicationUser.setLastname(user.lastname);
        applicationUser.getDetails().setTelNr(user.telNr);
        applicationUser.getDetails().getAddress().setStreet(user.street);
        applicationUser.getDetails().getAddress().setAreaCode(user.areaCode);
        applicationUser.getDetails().getAddress().setCity(user.city);

        return applicationUser;
    }

    @Override
    public Page<ApplicationUser> queryUsers(String fullname, Long matrNumber, Pageable pageable) {
        LOGGER.trace("Getting all users");
        return userRepository.findAllByFullnameOrMatrNumber(fullname, matrNumber, pageable);
    }
}
