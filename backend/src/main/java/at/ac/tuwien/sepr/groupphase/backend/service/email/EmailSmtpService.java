package at.ac.tuwien.sepr.groupphase.backend.service.email;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateStudentDto;

public interface EmailSmtpService {

    /**
     * Send a verification email to provided application user.
     *
     */
    public void sendVerificationEmail(CreateStudentDto dto);
}
