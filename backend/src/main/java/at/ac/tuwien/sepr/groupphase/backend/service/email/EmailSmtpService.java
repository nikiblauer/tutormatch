package at.ac.tuwien.sepr.groupphase.backend.service.email;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateStudentDto;

public interface EmailSmtpService {

    /**
     * Send a verification email to provided application user.
     *
     */
    public void sendVerificationEmail(CreateStudentDto dto, String origin);


    /**
     * Send a password reset email to provided email.
     *
     */
    void sendPasswordResetEmail(ApplicationUser email, String origin);
}
