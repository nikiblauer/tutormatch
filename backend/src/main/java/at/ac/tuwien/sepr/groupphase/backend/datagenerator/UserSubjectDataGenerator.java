package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubjectKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.ADMIN_EMAIL;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.ADMIN_NAME;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.USER_COUNT;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.USER_PASSWORD;


@Slf4j
@Profile("generateData")
@Component
@DependsOn({"subjectDataGenerator", "userDataGenerator"})
public class UserSubjectDataGenerator {

    private final UserSubjectRepository userSubjectRepository;

    @Autowired
    public UserSubjectDataGenerator(UserSubjectRepository userSubjectRepository) {
        this.userSubjectRepository = userSubjectRepository;
    }

    @PostConstruct
    private void generateUserSubjectRelation() {
        // check if data already exists
        if (userSubjectRepository.count() > 0) {
            log.info("User-Subject relations already generated. Skipping generation.");
            return;
        }
        log.info("Generating user-subject relations...");

        List<UserSubject> userSubjects = new ArrayList<>();

        // user and subjects are already inserted at this point
        for (Long userId = 1L; userId < USER_COUNT + 1; userId++) {
            Long subjectId = userId;

            //first four subjects as tutor
            while (subjectId < 5 + userId) {
                userSubjects.add(getUserSubject(userId, subjectId, "tutor"));
                subjectId++;
            }

            //next four subjects as trainee
            while (subjectId < 9 + userId) {
                userSubjects.add(getUserSubject(userId, subjectId, "trainee"));
                subjectId++;
            }
        }

        userSubjectRepository.saveAll(userSubjects);
        log.info("User-subject relations generation completed.");
    }

    private static UserSubject getUserSubject(Long userId, Long subjectId, String role) {
        var subject = new Subject();
        subject.setId(subjectId);
        var user = new ApplicationUser();
        user.setId(userId);
        UserSubjectKey id = new UserSubjectKey(user.getId(), subject.getId());
        return new UserSubject(id, user, subject, role);
    }
}