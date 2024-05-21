package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class SubjectValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void validateSubject(SubjectCreateDto subject) throws ValidationException {
        LOGGER.trace("validateSubject: subject:{}", subject);
        List<String> errors = new ArrayList<>();
        if (subject.getNumber() == null) {
            errors.add("Number cannot be null");
        }
        if (subject.getType() == null) {
            errors.add("Type cannot be null");
        }
        if (subject.getUrl() == null) {
            errors.add("Url cannot be null");
        }
        if (subject.getTitle() == null) {
            errors.add("Title cannot be null");
        }
        if (subject.getSemester() == null) {
            errors.add("Semester cannot be null");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Errors orroured while validating the subject", errors);
        }
    }
}
