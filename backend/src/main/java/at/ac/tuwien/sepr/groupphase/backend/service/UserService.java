package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswordResetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     */
    ApplicationUser findApplicationUserByEmail(String email);

    /**
     * Update a user.
     *
     * @param userEmail the email of the user
     * @param applicationUserDto the updated user
     * @return the updated user
     */
    ApplicationUser updateUser(String userEmail, UpdateStudentDto applicationUserDto) throws ValidationException;

    /**
     * Get all users.
     * Returns empty list of no users are given in the list
     *
     * @return a list of all users
     */
    Page<ApplicationUser> queryUsers(String fullname, Long matrNumber, Pageable pageable);

    ApplicationUser create(CreateStudentDto applicationUserDto) throws ValidationException;

    ApplicationUser findApplicationUserById(Long id);

    public List<String> getUserSubjectsByRole(Long id, String role);

    /**
     * sets the user verification status to verified.
     *
     * @return true if user was verified this way or false if the token was invalid (e.g. expired, wrong format)
     */
    boolean verifyEmail(String token);

    /**
     * sends a new verification email if user with this email exists and is not verified yet.
     *
     */
    void resendVerificationEmail(String email);
}
