package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.repository.FeedbackRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UserMatchService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Profile("generateData")
@Component
@DependsOn({"userDataGenerator", "chatRoomDataGenerator"})
public class FeedbackDataGenerator {

    private UserRepository userRepository;
    private FeedbackRepository feedbackRepository;
    private UserMatchService userMatchService;

    @Autowired
    public FeedbackDataGenerator(FeedbackRepository feedbackRepository, UserRepository userRepository, UserMatchService userMatchService) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.userMatchService = userMatchService;
    }

    @PostConstruct
    public void generateFeedback() {
        if (!feedbackRepository.findAll().isEmpty()) {
            log.info("Feedback data already generated. Skipping generation.");
            return;
        }

        log.info("Generating feedback...");
        List<ApplicationUser> users = userRepository.findAll();
        ArrayList<ApplicationUser> verifiedUsers = new ArrayList<>();

        for (ApplicationUser user : users) {
            if (user.getVerified() && !(user.getAdmin())) {
                verifiedUsers.add(user);
            }
        }

        for (int i = 0; i < verifiedUsers.size() - 1; i++) {
            ApplicationUser user1 = verifiedUsers.get(i);
            ApplicationUser user2 = verifiedUsers.get((i + 1) % verifiedUsers.size());

            if (userMatchService.findMatchingsForUser(user1.getDetails().getEmail()).anyMatch(userMatchDto -> user2.getId().equals(userMatchDto.getId()))) {
                feedbackRepository.save(new Feedback((long) (i + 1), user2.getId(), user1.getId(), "Feedback from user" + user1.getId() + " to user " + user2.getId(), new Date()));
            }

        }

    }

}
