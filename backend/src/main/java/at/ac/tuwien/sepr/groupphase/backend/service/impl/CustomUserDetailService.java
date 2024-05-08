package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserValidator validator;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer, UserValidator validator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.validator = validator;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOG.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return new User(applicationUser.getDetails().getEmail(), applicationUser.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOG.debug("Find application user by email");
        ApplicationUser applicationUser = userRepository.findApplicationUserByDetails_Email(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
        if (userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
        ) {
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }
        throw new BadCredentialsException("Username or password is incorrect or account is locked");
    }

    @Override
    public ApplicationUser create(ApplicationUserDto applicationUserDto) throws Exception {
        validator.verifyUserData(applicationUserDto);
        if (!userRepository.findAllByDetails_Email(applicationUserDto.email).isEmpty()) {
            throw new ValidationException("Email already exits please try an other one", new ArrayList<>());
        }
        ContactDetails details = new ContactDetails(
            applicationUserDto.telNr,
            applicationUserDto.email);
        ApplicationUser applicationUser = new ApplicationUser(
            applicationUserDto.password,
            false,
            applicationUserDto.firstname.trim().replaceAll("\\s+", " "),
            applicationUserDto.lastname.trim().replaceAll("\\s+", " "),
            applicationUserDto.matrNumber,
            details);
        return userRepository.save(applicationUser);
    }

    @Override
    public ApplicationUser updateUser(Long id, ApplicationUserDto applicationUserDto) throws ValidationException {
        LOG.trace("Updating user with id: {}", id);
        validator.verifyUserData(applicationUserDto);
        ApplicationUser applicationUser = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Could not find the user with the id " + id));

        applicationUser.setPassword(applicationUserDto.password);
        applicationUser.setFirstname(applicationUserDto.firstname);
        applicationUser.setLastname(applicationUserDto.lastname);
        applicationUser.setMatrNumber(applicationUserDto.matrNumber);
        applicationUser.getDetails().setEmail(applicationUserDto.email);
        applicationUser.getDetails().setTelNr(applicationUserDto.telNr);

        // Save the updated ApplicationUser in the database
        return userRepository.save(applicationUser);
    }

    @Override
    public List<ApplicationUser> queryUsers(String firstname, String lastname) {
        LOG.trace("Getting all users");
        List<ApplicationUser> applicationUsers = userRepository.findAll();
        if (firstname != null && !firstname.isEmpty()) {
            // make the search terms case-insensitive
            String lowerCaseFirstname = firstname.toLowerCase();
            applicationUsers = applicationUsers.stream()
                .filter(user -> user.getFirstname().toLowerCase().contains(lowerCaseFirstname))
                .collect(Collectors.toList());
        }
        if (lastname != null && !lastname.isEmpty()) {
            // make the search terms case-insensitive
            String lowerCaseLastname = lastname.toLowerCase();
            applicationUsers = applicationUsers.stream()
                .filter(user -> user.getLastname().toLowerCase().contains(lowerCaseLastname))
                .collect(Collectors.toList());
        }
        return applicationUsers;
    }
}
