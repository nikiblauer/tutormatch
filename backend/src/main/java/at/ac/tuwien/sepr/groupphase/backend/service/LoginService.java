package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswordResetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface LoginService {

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(UserLoginDto userLoginDto);

    /**
     * sends a password reset email to user if email exists.
     *
     * @param email the email of the user
     */
    void requestPasswordReset(String email);

    /**
     * changes token user password to new password if token is valid.
     *
     * @param token           the encoded email of the user
     * @param resetDto     the password to change to and repeat password avoid user mistakes
     *
     * @return true if password was changed this way or false if the token or password was invalid (e.g. expired, wrong format)
     */
    boolean changePasswordWithToken(String token, PasswordResetDto resetDto) throws ValidationException;

}
