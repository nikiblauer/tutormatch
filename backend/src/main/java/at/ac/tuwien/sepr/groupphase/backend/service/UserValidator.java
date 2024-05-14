package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String VALIDATION_PATTERN_1 = "^[a-zA-Z0-9.+-]+@student\\.tuwien\\.ac\\.at$";
    private static final String VALIDATION_PATTERN_2 = "^[a-zA-Z0-9.+-]+@tuwien\\.ac\\.at$";
    private static final String VALIDATION_PATTERN_3 = "^\\s+";
    private static final String VALIDATION_PATTERN_4 = "^(?:\\+?\\d⋅?){6,14}\\d$";


    public boolean validate(String email) {
        LOGGER.trace("Validation of user email to pattern: {}", email);
        return email.matches(VALIDATION_PATTERN_1) || email.matches(VALIDATION_PATTERN_2);
    }

    public void validateForCreate(CreateApplicationUserDto toCreate) throws ValidationException {
        LOGGER.trace("validateForCreate({})", toCreate);

        List<String> errors = new ArrayList<>();
        if (toCreate.email == null) {
            errors.add("Email cannot be null");
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
        if (!validate(toCreate.email)) {
            errors.add("Email not valid, does it end in tuwien.ac.at or student.tuwien.ac.at");
        }
        if (toCreate.email.length() > 255) {
            errors.add("Email is too long");
        }
        if (toCreate.firstname.isEmpty()) {
            errors.add("Name cannot be null");
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
        if (toCreate.firstname.matches(VALIDATION_PATTERN_3)) {
            errors.add("Name cannot be whitespace");
        }
        if (toCreate.lastname.isEmpty()) {
            errors.add("Name cannot be null");
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
        if (toCreate.lastname.matches(VALIDATION_PATTERN_3)) {
            errors.add("Name cannot be whitespace");
        }
        if (toCreate.password.length() < 8) {
            errors.add("Password has to be at least of length 8");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
    }

    public void verifyUserData(ApplicationUserDto user) throws ValidationException {
        LOGGER.trace("Validation of user: {}", user);
        List<String> errors = new ArrayList<>();
        if (user.email == null) {
            errors.add("Email cannot be null");
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
        if (!validate(user.email)) {
            errors.add("Email not valid, does it end in tuwien.ac.at or student.tuwien.ac.at");
        }
        if (user.email.length() > 255) {
            errors.add("Email is too long");
        }
        if (user.firstname.isEmpty()) {
            errors.add("Name cannot be null");
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
        if (user.firstname.matches(VALIDATION_PATTERN_3)) {
            errors.add("Name cannot be whitespace");
        }
        if (user.lastname.isEmpty()) {
            errors.add("Name cannot be null");
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
        if (user.lastname.matches(VALIDATION_PATTERN_3)) {
            errors.add("Name cannot be whitespace");
        }
        if (user.password.length() < 8) {
            errors.add("Password has to be at least of length 8");
        }
        if (!user.telNr.matches(VALIDATION_PATTERN_4)) {
            if (!user.telNr.isEmpty()) {
                errors.add("Telephone number has to be a valid phone number");
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
    }
}
