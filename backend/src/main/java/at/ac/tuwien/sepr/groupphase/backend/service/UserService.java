package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentAsAdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Banned;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
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
     * @param userEmail          the email of the user
     * @param applicationUserDto the updated user
     * @return the updated user
     */
    ApplicationUser updateUser(String userEmail, UpdateStudentDto applicationUserDto) throws ValidationException;

    /**
     * Updates a User including the Matriculation Number.
     *
     * @param userEmail          the email of tue user
     * @param applicationUserDto the updated user
     * @return the updated user
     * @throws ValidationException If any validation errors occur. (no name, ...)
     */
    ApplicationUser updateUserIncludingMatrNr(String userEmail, UpdateStudentAsAdminDto applicationUserDto) throws ValidationException;

    /**
     * Get all users.
     * Returns empty list of no users are given in the list
     *
     * @return a list of all users
     */
    Page<ApplicationUser> queryUsers(String fullname, Long matrNumber, Boolean hasBan, Pageable pageable);

    /**
     * Creates a new User in the database.
     *
     * @param applicationUserDto The user that is created
     * @return The created User Entry in the database
     * @throws ValidationException If any validation errors occur. (no name, ...)
     */
    ApplicationUser create(CreateStudentDto applicationUserDto) throws ValidationException;

    /**
     * Finds a User with a given ID.
     *
     * @param id the user to find
     * @return An entity of the User with the given id
     */

    ApplicationUser findApplicationUserById(Long id);

    /**
     * Gets a list of all subjects a User has a certain role.
     *
     * @param id   the user id
     * @param role the searched role (trainee/tutor)
     * @return a list of Subjects
     */
    List<String> getUserSubjectsByRole(Long id, String role);

    /**
     * sets the user verification status to verified.
     *
     * @return true if user was verified this way or false if the token was invalid (e.g. expired, wrong format)
     */
    boolean verifyEmail(String token);

    /**
     * sends a new verification email if user with this email exists and is not verified yet.
     */
    void resendVerificationEmail(String email);

    /**
     * Bans a user by creating a new Ban entity in the database.
     *
     * @param id     The ID of the user to be banned.
     * @param reason The reason for the ban.
     */
    void banUser(Long id, String reason);


    /**
     * Retrieves the ban information for a specific user.
     *
     * @param id The ID of the user for whom to retrieve the ban information.
     * @return The Ban entity representing the ban information for the user.
     * @throws NotFoundException if no ban is found for the specified user ID.
     */
    Banned getBanForUser(Long id);

    /**
     * gets the boolean value of the visibility for a user.
     *
     * @param user the user of the boolean flag
     * @return a boolean flag true for visible false for not visible.
     */
    boolean getVisibility(ApplicationUser user);

    /**
     * updates the visibility for a user.
     *
     * @param flag the value to update the visibility with
     * @param user the user for whom the visibility is updated.
     */
    void updateVisibility(boolean flag, ApplicationUser user);

    /**
     * Blocks a user if the user is not already blocked.
     * Blocking takes place in info modal in chat room
     *
     * @param userId        the user who wants to block another user
     * @param userIdToBlock the user who should be blocked
     */
    void blockUser(Long userId, Long userIdToBlock);

    /**
     * Unblocks a user if the user is blocked.
     *
     * @param userIdToUnblock the user who should be unblocked
     * @param userId the user who wants to unblock another user
     */
    void unblockUser(Long userId, Long userIdToUnblock);


    /**
     * Gets a list of all user IDs that the user with the given ID has blocked.
     *
     * @param userId the user for whom to retrieve the blocked users
     * @return a list of user IDs that the user has blocked
     */
    List<Long> getBlockedUsers(Long userId);
}
