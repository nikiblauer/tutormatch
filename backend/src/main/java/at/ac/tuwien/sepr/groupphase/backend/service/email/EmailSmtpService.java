package at.ac.tuwien.sepr.groupphase.backend.service.email;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;

public interface EmailSmtpService {

    /**
     * Send a verification email to provided application user.
     *
     */
    public void sendVerificationEmail(CreateApplicationUserDto dto);

    /**
     * Send a password reset email to provided email.
     *
     */
    void sendPasswordResetEmail(ApplicationUser email);
}
