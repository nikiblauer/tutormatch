package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.*;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.*;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.RatingDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.SubjectDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.UserDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.UserSubjectDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.repository.RatingRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UserMatchService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Autowired
    private FeedbackDataGenerator feedbackDataGenerator;

    @Autowired
    private FeedbackRepository feedbackRepository;


    @BeforeEach
    public void setUp() throws IOException {
        generateData();
    }


    @AfterEach
    public void tearDown() {
        clearData();
    }

    private void generateData() throws IOException {
        userDataGenerator.generateApplicationUser();
        subjectDataGenerator.generateSubjects();
        userSubjectDataGenerator.generateUserSubjectRelation();
        chatRoomDataGenerator.generateChatRooms();
        chatMessageDataGenerator.generateChatMessages();
        feedbackDataGenerator.generateFeedback();
        ratingDataGenerator.generateUserSubjectRelation();
    }

    private void clearData() {
        chatMessageRepository.deleteAll();
        chatRoomRepository.deleteAll();
        feedbackRepository.deleteAll();
        userSubjectRepository.deleteAll();
        userRepository.deleteAll();
        subjectRepository.deleteAll();
        ratingRepository.deleteAll();
    }
}
