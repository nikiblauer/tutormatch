package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.*;
import at.ac.tuwien.sepr.groupphase.backend.repository.*;
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
    }

    private void clearData() {
        chatMessageRepository.deleteAll();
        chatRoomRepository.deleteAll();

        userSubjectRepository.deleteAll();
        userRepository.deleteAll();
        subjectRepository.deleteAll();

    }
}
