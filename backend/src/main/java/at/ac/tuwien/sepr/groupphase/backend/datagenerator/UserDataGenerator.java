package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.ADMIN_EMAIL;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.ADMIN_NAME;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.USER_COUNT;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.USER_PASSWORD;


@Slf4j
@Profile({"test", "generateData"})
@Component
public class UserDataGenerator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void generateApplicationUser() {
        // check if data already exists
        if (userRepository.existsById(1L)) {
            log.info("User data already generated. Skipping generation.");
            return;
        }

        log.info("Generating user data...");
        //generate 10 user
        var users = generateUsers(USER_COUNT);
        userRepository.saveAll(Arrays.asList(users));

        //generate admin account
        String userPassword = passwordEncoder.encode(USER_PASSWORD);
        ApplicationUser admin = new ApplicationUser(userPassword, true, ADMIN_NAME.split(" ")[0], ADMIN_NAME.split(" ")[1], null,
            new ContactDetails("+43660 3333333", ADMIN_EMAIL));
        userRepository.save(admin);
        log.info("User data generation completed.");
    }


    private ApplicationUser[] generateUsers(int count) {
        ApplicationUser[] users = new ApplicationUser[count];
        String password = passwordEncoder.encode(USER_PASSWORD);
        for (int i = 0; i < count; i++) {
            long matrNumber = 10000001L + i;
            var user = new ApplicationUser(password, false, "User" + (i + 1), "Surname" + (i + 1),
                matrNumber, new ContactDetails("+43660 1111111", "e" + matrNumber + "@student.tuwien.ac.at"));
            users[i] = user;
        }
        return users;
    }

}