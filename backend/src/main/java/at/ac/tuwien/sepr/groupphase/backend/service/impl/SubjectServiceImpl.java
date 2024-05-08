package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubjectKey;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SubjectService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserSubjectValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserSubjectRepository userSubjectRepository;
    private final SubjectRepository subjectRepository;
    private final UserSubjectValidator validator;

    public SubjectServiceImpl(UserSubjectRepository userSubjectRepository, SubjectRepository subjectRepository, UserSubjectValidator validator) {
        this.userSubjectRepository = userSubjectRepository;
        this.subjectRepository = subjectRepository;
        this.validator = validator;
    }

    @Override
    public void setUserSubjects(ApplicationUser student, List<Long> trainees, List<Long> tutors) throws ValidationException {
        LOGGER.trace("choose subjects with user: {}, trainee: {}, tutor: {}", student, trainees, tutors);
        validator.validateSubjectSelection(trainees, tutors, student);
        List<UserSubject> keys = userSubjectRepository.getUserSubjectByUser(student);
        for (UserSubject key : keys) {
            if (key != null) {
                userSubjectRepository.removeUserSubjectById(key.getId());
            }
        }
        List<Subject> traineeList = findSubjectById(trainees);
        List<Subject> tutorList = findSubjectById(tutors);
        for (Subject subject : traineeList) {
            userSubjectRepository.save(new UserSubject(new UserSubjectKey(student.getId(), subject.getId()), student, subject, "trainee"));
        }
        for (Subject subject : tutorList) {
            userSubjectRepository.save(new UserSubject(new UserSubjectKey(student.getId(), subject.getId()), student, subject, "tutor"));
        }
    }

    @Override
    public List<Subject> findSubjectById(List<Long> ids) {
        LOGGER.trace("findSubjectById: {}", ids);
        List<Subject> list = new ArrayList<>();
        for (Long id : ids) {
            list.add(subjectRepository.findSubjectById(id));
        }
        return list;
    }
}
