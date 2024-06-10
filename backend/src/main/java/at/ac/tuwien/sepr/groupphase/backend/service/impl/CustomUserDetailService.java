package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentAsAdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Banned;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.BanRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RatingRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.UserValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.email.EmailSmtpService;
import jakarta.transaction.Transactional;
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
    private final BanRepository banRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserValidator validator;
    private final EmailSmtpService emailService;
    private final RatingRepository ratingRepository;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, BanRepository banRepository,
                                   PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer,
                                   UserValidator validator, EmailSmtpService emailService,
                                   RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.banRepository = banRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.validator = validator;
        this.emailService = emailService;
        this.ratingRepository = ratingRepository;
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
    @Transactional
    public void banUser(Long id, String reason) {
        LOGGER.trace("Banning user with id: {}", id);
        ApplicationUser applicationUser = userRepository.findById(id).orElse(null);
        if (applicationUser == null) {
            throw new NotFoundException(String.format("User with id %d not found", id));
        }

        if (applicationUser.isBanned()) {
            // user already banned
            return;
        }

        //delete ratings of student
        ratingRepository.deleteAllByStudentId(id);

        Banned userBan = new Banned();
        userBan.setUser(applicationUser);
        userBan.setReason(reason);
        banRepository.save(userBan);
    }

    @Override
    public Banned getBanForUser(Long id) {
        LOGGER.trace("Get Ban for user with id: {}", id);
        Banned userBan = banRepository.getBanByUserId(id);
        if (userBan == null) {
            throw new NotFoundException(String.format("No Ban for User with id %d found", id));
        }

        return userBan;
    }

    @Override
    public ApplicationUser updateUser(String userEmail, UpdateStudentDto applicationUserDto) throws ValidationException {
        LOGGER.trace("Updating user with email: {}", userEmail);
        //remove whitespaces from telNr
        applicationUserDto.telNr = applicationUserDto.telNr.trim();
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
    public ApplicationUser updateUserIncludingMatrNr(String userEmail, UpdateStudentAsAdminDto applicationUserDto) throws ValidationException {
        LOGGER.trace("Updating user with email: {}", userEmail);
        //remove whitespaces from telNr
        applicationUserDto.telNr = applicationUserDto.telNr.trim();
        validator.verifyUserData(applicationUserDto);

        ApplicationUser applicationUser = userRepository.findApplicationUserByDetails_Email(userEmail);
        if (applicationUser == null) {
            throw new NotFoundException(String.format("User with email %s not found", userEmail));
        }

        applicationUser.setFirstname(applicationUserDto.firstname);
        applicationUser.setLastname(applicationUserDto.lastname);
        applicationUser.setMatrNumber(applicationUserDto.matrNumber);
        applicationUser.getDetails().setTelNr(applicationUserDto.telNr);
        applicationUser.getDetails().getAddress().setStreet(applicationUserDto.street);
        applicationUser.getDetails().getAddress().setAreaCode(applicationUserDto.areaCode);
        applicationUser.getDetails().getAddress().setCity(applicationUserDto.city);

        // Save the updated ApplicationUser in the database
        return userRepository.save(applicationUser);
    }


    @Override
    public Page<ApplicationUser> queryUsers(String fullname, Long matrNumber, Boolean hasBan, Pageable pageable) {
        LOGGER.trace("Getting all users");
        return userRepository.findAllByFullnameOrMatrNumber(fullname, matrNumber, hasBan, pageable);
    }
}
