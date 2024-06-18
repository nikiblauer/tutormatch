package at.ac.tuwien.sepr.groupphase.backend.service;


import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.entity.Report;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface ReportService {
    /**
     * this methode is used to report a user by another user.
     *
     * @param reporter     the user that reports
     * @param reportedUser the user that is being reported
     * @param reason       the reason for reporting the user reportedUser
     * @throws ValidationException an exception if the reason is too long for the SQL database
     */
    void reportUser(ApplicationUser reporter, ApplicationUser reportedUser, String reason) throws ValidationException;

    /**
     * this methode is used to report a user by another user and the feedback from that user.
     *
     * @param reporter     the user that reports
     * @param reportedUser the user that is being reported
     * @param reason       the reason for reporting the user reportedUser
     * @param feedback     the feedback that was being reported
     * @throws ValidationException an exception if the reason is too long for the SQL database
     */
    void reportUserFeedback(ApplicationUser reporter, ApplicationUser reportedUser, String reason, Feedback feedback) throws ValidationException;

    /**
     * this methode is used to report a user by another user and the chat from that user with the reporter chat.
     *
     * @param reporter the user that reports
     * @param chatId   the chat that is being reported
     * @param reason   the reason for reporting the user reportedUser
     * @throws ValidationException an exception if the reason is too long for the SQL database
     */
    void reportUserChat(String chatId, ApplicationUser reporter, String reason) throws ValidationException;

    /**
     * gets all reports from the database.
     *
     * @return a list with all reports stored in the database
     */
    List<Report> getAllReports();

    /**
     * deletes a report with the corresponding id.
     *
     * @param id the id of the report that is going to be deleted
     */
    void deleteReport(Long id);
}
