package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserSubjectValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void validateSubjectSelection(List<Long> traineesSubjects, List<Long> tutorsSubjects, ApplicationUser student) throws ValidationException {
        LOGGER.trace("Verifying data for UserSubjects: student: {}, trainee: {}, tutor: {}", student, traineesSubjects, tutorsSubjects);
        List<String> errors = new ArrayList<>();
        if (tutorsSubjects == null | traineesSubjects == null) {
            errors.add("Invalid DataType");
            throw new ValidationException("Error while validation Data for choosing Subjects", errors);
        }
        for (Long trainee : traineesSubjects) {
            if (trainee == null) {
                errors.add("Invalid DataType");
                throw new ValidationException("Error while validation Data for choosing Subjects", errors);
            }
        }
        for (Long tutor : tutorsSubjects) {
            if (tutor == null) {
                errors.add("Invalid DataType");
                throw new ValidationException("Error while validation Data for choosing Subjects", errors);
            }
        }

        for (Long trainee : traineesSubjects) {
            if (tutorsSubjects.contains(trainee)) {
                errors.add("You cannot be trainee and tutor in the same subject");
                throw new ValidationException("Error while validation Data for choosing Subjects", errors);
            }
        }
    }
}
