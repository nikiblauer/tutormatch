package at.ac.tuwien.sepr.groupphase.backend.service.validators;

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
    private static final String VALIDATION_PATTERN = "^\\s*";

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
            throw new ValidationException(errors.toString());
        }
        if (subject.getNumber().matches(VALIDATION_PATTERN)) {
            errors.add("Number may not consists only of whitespaces");
        }
        if (subject.getType().matches(VALIDATION_PATTERN)) {
            errors.add("Number may not consists only of whitespaces");
        }
        if (subject.getTitle().matches(VALIDATION_PATTERN)) {
            errors.add("Number may not consists only of whitespaces");
        }
        if (subject.getSemester().matches(VALIDATION_PATTERN)) {
            errors.add("Number may not consists only of whitespaces");
        }
        if (subject.getNumber().length() > 255) {
            errors.add("Number name has to be smaller than 255 characters");
        }
        if (subject.getSemester().length() > 255) {
            errors.add("Semester name has to be smaller than 255 characters");
        }
        if (subject.getTitle().length() > 255) {
            errors.add("Title has to be smaller than 255 characters");
        }
        if (subject.getType().length() > 255) {
            errors.add("Type has to be smaller than 255 characters");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}
