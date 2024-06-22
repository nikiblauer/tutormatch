package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.config.MyAppConfig;
import at.ac.tuwien.sepr.groupphase.backend.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Banned;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;

import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.ADMIN_EMAIL;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.ADMIN_NAME;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.BANNED_USER_EMAIL;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.USER_COUNT;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.USER_PASSWORD;


@Slf4j
@Profile("generateData")
@Component
public class UserDataGenerator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private MyAppConfig config;

    private static final String[] FIRST_NAMES = {
        "Elias", "Güney", "Konstantin", "Martin", "Niklas", "Yaroslav",
        "Alex", "Will", "John", "Emily", "Sarah", "Michael", "Melih", "Jessica", "David", "Laura", "James",
        "Andrew", "Tom", "Megan", "Emma", "Daniel", "Sophia", "Chris", "Olivia", "Oliver", "Ryan", "Hannah",
        "Ethan", "Chloe", "Joshua", "Zoe", "Jacob", "Samantha", "Noah", "Ava", "Matthew", "Isabella",
        "Anthony", "Grace", "Joseph", "Natalie", "Samuel", "Alyssa", "Benjamin", "Sophia", "William", "Evelyn"
    };

    private static final String[] LAST_NAMES = {
        "Moser", "Erdogan", "Unterweger", "Harhammer", "Rychkov",
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Cosgun", "Miller", "Davis", "Rodriguez", "Martinez",
        "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Mähr", "Moore", "Jackson", "Martin",
        "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson",
        "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores"
    };

    private static final String[] VIENNA_STREETS = {
        "Stephansplatz", "Ringstraße", "Kärntner Straße", "Graben", "Mariahilfer Straße",
        "Landstraßer Hauptstraße", "Neubaugasse", "Margaretenstraße", "Praterstraße", "Wiedner Hauptstraße",
        "Josefstädter Straße", "Schönbrunner Straße", "Hernals Hauptstraße", "Gürtel", "Hietzinger Hauptstraße",
        "Alser Straße", "Leopoldsgasse", "Donaukanalstraße", "Taborstraße", "Brunnengasse"
    };

    private static final Integer[] VIENNA_AREA_CODES = {
        1010, 1020, 1030, 1040, 1050,
        1060, 1070, 1080, 1090, 1100,
        1110, 1120, 1130, 1140, 1150,
        1160, 1170, 1180, 1190, 1200,
        1210, 1220, 1230
    };


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
        var users = generateUsers(this.config.getUserCount());
        userRepository.saveAll(Arrays.asList(users));

        //generate admin account
        String userPassword = passwordEncoder.encode(USER_PASSWORD);
        ApplicationUser admin = new ApplicationUser(userPassword, true, ADMIN_NAME.split(" ")[0], ADMIN_NAME.split(" ")[1], null,
            new ContactDetails("+43660 3333333", ADMIN_EMAIL, null), true);
        userRepository.save(admin);

        //generate Banned User
        ApplicationUser bannedUser = createUser(userPassword, "UserBanned", "SurnameBanned", 11000001L,
            "Teststraße 1111", 1001, "+43660 1111111", "Wien", false);
        bannedUser.getDetails().setEmail(BANNED_USER_EMAIL);

        var ban = new Banned();
        ban.setUser(bannedUser);
        ban.setReason("TestBan");
        bannedUser.setBan(ban);
        userRepository.save(bannedUser);

        log.info("User data generation completed.");
    }


    private ApplicationUser[] generateUsers(int count) {
        ApplicationUser[] users = new ApplicationUser[count];
        String password = passwordEncoder.encode(USER_PASSWORD);
        Random random = new Random();
        if (config.getSeed() != null) {
            random = new Random(config.getSeed());
        }

        for (int i = 0; i < count; i++) {
            long matrNumber = 10000001L + i;
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String street = VIENNA_STREETS[random.nextInt(VIENNA_STREETS.length)];
            Integer areaCode = VIENNA_AREA_CODES[random.nextInt(VIENNA_AREA_CODES.length)];
            String telNr = "+" + (436601111110L + i);
            String city = "Wien";
            //last user is not verified
            boolean isVerified = i != count - 1;

            users[i] = createUser(password, firstName, lastName, matrNumber, street, areaCode, telNr, city, isVerified);
        }
        return users;
    }

    private ApplicationUser createUser(String password, String firstName, String lastName, long matrNumber,
                                       String street, Integer areaCode, String telNr, String city, boolean isVerified) {
        return new ApplicationUser(
            password, false, firstName, lastName, matrNumber,
            new ContactDetails(telNr, "e" + matrNumber + "@student.tuwien.ac.at", new Address(street, areaCode, city)),
            isVerified
        );
    }


}