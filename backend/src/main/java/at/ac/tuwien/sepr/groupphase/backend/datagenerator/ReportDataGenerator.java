package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.entity.Report;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatRoomRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.FeedbackRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ReportRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Profile("generateData")
@Component
@DependsOn({"userDataGenerator", "feedbackDataGenerator"})
public class ReportDataGenerator {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ReportDataGenerator(ReportRepository reportRepository, UserRepository userRepository, FeedbackRepository feedbackRepository, ChatRoomRepository chatRoomRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    @PostConstruct
    public void generateReports() {
        if (reportRepository.count() > 0) {
            log.info("Reports are already generated. Skipping generation.");
            return;
        }
        log.info("Generating reports...");
        List<Feedback> feedbacks = feedbackRepository.findAll();
        List<ApplicationUser> applicationUsers = userRepository.findAll()
            .stream().filter(item -> !item.isBanned() && item.getVerified() && !item.getAdmin())
            .toList();
        Feedback f = feedbacks.getFirst();
        Report r = new Report();
        r.setReporter(applicationUsers.getFirst());
        r.setReportedUser(applicationUsers.get(1));
        r.setReportedFeedback(f);
        r.setReason("Test reason!!");
        reportRepository.save(r);
        r = new Report();
        r.setReporter(applicationUsers.getFirst());
        r.setReportedUser(applicationUsers.get(1));
        r.setReason("Report user");
        reportRepository.save(r);
        r = new Report();
        r.setReporter(applicationUsers.getFirst());
        r.setReportedUser(applicationUsers.get(1));
        List<ChatRoom> c = chatRoomRepository.findAllBySenderId(applicationUsers.get(1).getId());
        r.setChatRoomId(c.getFirst().getChatRoomId());
        reportRepository.save(r);
        log.info("Reports data generation completed.");

    }

}
