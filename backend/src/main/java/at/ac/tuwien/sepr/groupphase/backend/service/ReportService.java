package at.ac.tuwien.sepr.groupphase.backend.service;


import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface ReportService {
    void reportUser(ApplicationUser reporter, ApplicationUser reportedUser, String reason) throws ValidationException;
    void reportUserFeedback(ApplicationUser reporter, ApplicationUser reportedUser, String reason, Feedback feedback) throws ValidationException;
    void reportUserChat(ApplicationUser reporter, ApplicationUser chat, String reason);
}
