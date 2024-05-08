package at.ac.tuwien.sepr.groupphase.backend;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.UserDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class TestInit {

    @Autowired
    private UserDataGenerator userDataGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSubjectRepository userSubjectRepository;

    @BeforeEach
    public void setUp() {
        // Clear the associated records in the USER_SUBJECT table
        userSubjectRepository.deleteAll();

        // Clear the database
        userRepository.deleteAll();

        // Generate fresh test data
        userDataGenerator.generateApplicationUser();
    }
}
