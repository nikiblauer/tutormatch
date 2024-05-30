package at.ac.tuwien.sepr.groupphase.backend.basetest;

<<<<<<< backend/src/test/java/at/ac/tuwien/sepr/groupphase/backend/basetest/BaseTest.java
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.*;
import at.ac.tuwien.sepr.groupphase.backend.repository.*;
=======
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.RatingDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.SubjectDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.UserDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.UserSubjectDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.repository.RatingRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
>>>>>>> backend/src/test/java/at/ac/tuwien/sepr/groupphase/backend/basetest/BaseTest.java
import at.ac.tuwien.sepr.groupphase.backend.service.UserMatchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class BaseTest {

    @Autowired
    protected UserMatchService userMatchService;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserSubjectRepository userSubjectRepository;
    @Autowired
    private UserDataGenerator userDataGenerator;
    @Autowired
    private UserSubjectDataGenerator userSubjectDataGenerator;
    @Autowired
    private SubjectDataGenerator subjectDataGenerator;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private RatingDataGenerator ratingDataGenerator;

    @Autowired
    private ChatRoomDataGenerator chatRoomDataGenerator;

    @Autowired
    private ChatMessageDataGenerator chatMessageDataGenerator;


    @BeforeEach
    public void setUp() throws IOException {
        generateData();
    }

    @AfterEach
    public void tearDown() throws IOException {
        clearData();
    }

    private void generateData() throws IOException {
        userDataGenerator.generateApplicationUser();
        subjectDataGenerator.generateSubjects();
        userSubjectDataGenerator.generateUserSubjectRelation();
        chatRoomDataGenerator.generateChatRooms();
        chatMessageDataGenerator.generateChatMessages();
        ratingDataGenerator.generateUserSubjectRelation();
    }

    private void clearData() {
        chatMessageRepository.deleteAll();
        chatRoomRepository.deleteAll();

        userSubjectRepository.deleteAll();
        userRepository.deleteAll();
        subjectRepository.deleteAll();

        ratingRepository.deleteAll();
    }
}
