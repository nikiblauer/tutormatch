package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubjectKey;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SubjectService;
import at.ac.tuwien.sepr.groupphase.backend.service.SubjectValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.UserSubjectValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final SubjectValidator subjectValidator;

    public SubjectServiceImpl(UserSubjectRepository userSubjectRepository, SubjectRepository subjectRepository, UserSubjectValidator validator, SubjectValidator subjectValidator) {
        this.userSubjectRepository = userSubjectRepository;
        this.subjectRepository = subjectRepository;
        this.validator = validator;
        this.subjectValidator = subjectValidator;
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

    @Override
    public List<UserSubject> findSubjectsByUser(ApplicationUser user) {
        LOGGER.trace("findSubjectsByUserId: user {}", user);
        return userSubjectRepository.getUserSubjectByUser(user);
    }

    @Override
    public Page<Subject> findSubjectsBySearchParam(String searchParam, Pageable pageable) {
        LOGGER.trace("findSubjectsByQuery: searchParam {}", searchParam);
        return subjectRepository.findAllSubjectByQueryParam(searchParam, pageable);
    }

    @Override
    public Subject updateSubject(SubjectDetailDto subject) throws Exception {
        LOGGER.trace("updateSubject: subject{}", subject);
        subjectValidator.validateSubject(subject);
        Subject s = subjectRepository.findSubjectById(subject.getId());
        if (s == null) {
            throw new NotFoundException(String.format("Subject with id %d not found", subject.getId()));
        }
        safeSubjectDetailDto(subject, s);
        return s;
    }

    @Override
    public Subject deleteSubject(Long id) {
        LOGGER.trace("deleteSubject: id{}", id);
        Subject s = subjectRepository.findSubjectById(id);
        if (s == null) {
            throw new NotFoundException(String.format("Subject with id %d not found", id));
        }
        userSubjectRepository.deleteUserSubjectsBySubject(s);
        subjectRepository.deleteById(id);
        return s;
    }

    @Override
    public Subject createSubject(SubjectCreateDto subject) throws ValidationException {
        LOGGER.trace("updateSubject: subject{}", subject);
        subjectValidator.validateSubject(subject);
        Subject s = new Subject();
        safeSubjectDetailDto(subject, s);
        return s;
    }

    private void safeSubjectDetailDto(SubjectCreateDto subject, Subject s) {
        LOGGER.trace("safeSubjectDetailDto: subjectDetailDto{}, subject:{}", subject, s);
        s.setDescription(subject.getDescription());
        s.setTitle(subject.getTitle());
        s.setType(subject.getType());
        s.setNumber(subject.getNumber());
        s.setSemester(subject.getSemester());
        s.setUrl(subject.getUrl());
        subjectRepository.save(s);
    }
}
