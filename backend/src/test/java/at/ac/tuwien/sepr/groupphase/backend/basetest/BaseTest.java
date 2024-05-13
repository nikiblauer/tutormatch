package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.SubjectDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.UserDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.UserSubjectDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UserMatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;


public class BaseTest {
    @Autowired
    protected UserMatchService userMatchService;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private UserSubjectRepository userSubjectRepository;
    @Autowired
    private UserDataGenerator userDataGenerator;
    @Autowired
    private UserSubjectDataGenerator userSubjectDataGenerator;
    @Autowired
    private SubjectDataGenerator subjectDataGenerator;

    @BeforeEach
    public void setUp() throws IOException {
        userSubjectRepository.deleteAll();
        userRepository.deleteAll();
        subjectRepository.deleteAll();

        userDataGenerator.generateApplicationUser();
        subjectDataGenerator.generateSubjects();
        userSubjectDataGenerator.generateUserSubjectRelation();
    }
}
