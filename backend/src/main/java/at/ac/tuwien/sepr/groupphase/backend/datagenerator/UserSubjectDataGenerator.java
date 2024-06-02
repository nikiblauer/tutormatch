package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubjectKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Profile("generateData")
@Component
@DependsOn({"subjectDataGenerator", "userDataGenerator"})
public class UserSubjectDataGenerator {

    private final UserSubjectRepository userSubjectRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public UserSubjectDataGenerator(UserSubjectRepository userSubjectRepository, UserRepository userRepository, SubjectRepository subjectRepository) {
        this.userSubjectRepository = userSubjectRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
    }

    @PostConstruct
    public void generateUserSubjectRelation() {
        // check if data already exists
        if (userSubjectRepository.count() > 0) {
            log.info("User-Subject relations already generated. Skipping generation.");
            return;
        }
        log.info("Generating user-subject relations...");

        List<UserSubject> userSubjects = new ArrayList<>();
        var subjects = subjectRepository.findAll();

        int i = 0;

        // user and subjects are already inserted at this point
        for (ApplicationUser applicationUser : userRepository.findAll()) {

            if (applicationUser.getAdmin() || !applicationUser.getVerified()) {
                continue;
            }

            var role1 = i % 2 == 1 ? "tutor" : "trainee";
            //first four subjects as tutor
            for (int j = i; j < 4 + i; j++) {
                userSubjects.add(getUserSubject(applicationUser.getId(), subjects.get(j).getId(), role1));
            }

            var role2 = i % 2 == 1 ? "trainee" : "tutor";
            //next four subjects as trainee
            for (int j = 4 + i; j < 8 + i; j++) {
                userSubjects.add(getUserSubject(applicationUser.getId(), subjects.get(j).getId(), role2));
            }
            i++;
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