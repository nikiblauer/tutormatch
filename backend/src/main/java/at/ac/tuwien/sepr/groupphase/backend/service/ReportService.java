package at.ac.tuwien.sepr.groupphase.backend.service;


import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.entity.Report;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface ReportService {
    void reportUser(ApplicationUser reporter, ApplicationUser reportedUser, String reason) throws ValidationException;

    void reportUserFeedback(ApplicationUser reporter, ApplicationUser reportedUser, String reason, Feedback feedback) throws ValidationException;

    void reportUserChat(String chatId, ApplicationUser reporter, String reason) throws ValidationException;

    List<Report> getAllReports();

    void deleteReport(Long id);
}
