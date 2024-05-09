package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface SubjectService {
    void setUserSubjects(ApplicationUser student, List<Long> traineeSubjects, List<Long> tutorSubjects) throws ValidationException;


    List<Subject> findSubjectById(List<Long> ids);
}
