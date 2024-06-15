package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.entity.Report;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ReportRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ReportService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ReportServiceImpl(ReportRepository reportRepository, UserService userService) {
        this.reportRepository = reportRepository;
        this.userService = userService;
    }

    @Override
    public void reportUser(ApplicationUser reporter, ApplicationUser reportedUser, String reason) throws ValidationException {
        LOGGER.trace("reportUser({},{},{})", reporter, reportedUser, reason);
        validateReason(reason);
        Report r = new Report();
        r.setReporter(reporter);
        r.setReportedUser(reportedUser);
        r.setReason(reason);
        reportRepository.save(r);
    }

    @Override
    public void reportUserFeedback(ApplicationUser reporter, ApplicationUser reportedUser, String reason, Feedback feedback) throws ValidationException {
        LOGGER.trace("reportUserFeedback({},{},{},{})", reporter, reportedUser, reason, feedback);
        if (feedback == null){
            throw new NotFoundException("The reported Feedback does not exist");
        }
        validateReason(reason);
        Report r = new Report();
        r.setReporter(reporter);
        r.setReportedUser(reportedUser);
        r.setReason(reason);
        r.setReportedFeedback(feedback);
        reportRepository.save(r);
    }

    @Override
    public void reportUserChat(ApplicationUser reporter, ApplicationUser chat, String reason) {

    }

    @Override
    public List<Report> getAllReports() {
        return this.reportRepository.findAll();
    }

    private void validateReason(String reason) throws ValidationException {
        LOGGER.trace("validateReason: {}", reason);
        if (reason.length() > 100){
            throw new ValidationException("Pleas keep your reason short");
        }
    }
}
