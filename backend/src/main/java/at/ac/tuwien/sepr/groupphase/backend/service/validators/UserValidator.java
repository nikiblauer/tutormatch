package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswordResetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserValidator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    public boolean validate(String email) {
        LOGGER.trace("Validation of user email to pattern: {}", email);
        return email.matches(VALIDATION_PATTERN_1) || email.matches(VALIDATION_PATTERN_2);
    }

    public void validateForCreate(CreateStudentDto toCreate) throws ValidationException {
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
        if (toCreate.firstname.length() > 255) {
            errors.add("First name has to be smaller than 255 characters");
        }
        if (toCreate.lastname.length() > 255) {
            errors.add("Last name has to be smaller than 255 characters");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
    }

    public void verifyUserData(StudentDto user) throws ValidationException {
        LOGGER.trace("Validation of user: {}", user);
        List<String> errors = new ArrayList<>();

        if (user.password.length() < 8) {
            errors.add("Password has to be at least of length 8");
        }

        validateUserData(user.firstname, user.lastname, user.telNr, errors);
    }

    public void verifyUserData(UpdateStudentDto user) throws ValidationException {
        LOGGER.trace("Validation of user: {}", user);
        List<String> errors = new ArrayList<>();
        validateUserData(user.firstname, user.lastname, user.telNr, errors);
    }

    private void validateUserData(String firstname, String lastname, String telNr, List<String> errors) throws ValidationException {
        if (firstname.isEmpty()) {
            errors.add("Name cannot be null");
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
        if (firstname.matches(VALIDATION_PATTERN_3)) {
            errors.add("Name cannot be whitespace");
        }
        if (lastname.isEmpty()) {
            errors.add("Name cannot be null");
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
        if (lastname.matches(VALIDATION_PATTERN_3)) {
            errors.add("Name cannot be whitespace");
        }

        if (!telNr.matches(VALIDATION_PATTERN_4)) {
            if (!telNr.isEmpty()) {
                errors.add("Telephone number has to be a valid phone number");
            }
        }
        if (firstname.length() > 255) {
            errors.add("First name has to be smaller than 255 characters");
        }
        if (lastname.length() > 255) {
            errors.add("Last name has to be smaller than 255 characters");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }

    public void validatePasswordChange(PasswordResetDto resetDto, String oldEncodedPassword) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (!resetDto.password.equals(resetDto.repeatPassword)) {
            errors.add("Passwords must match");
        }

        if (resetDto.password.length() < 8) {
            errors.add("Password has to be at least of length 8");
        }
        if (passwordEncoder.matches(resetDto.getPassword(), oldEncodedPassword)) {
            errors.add("New password can not be equal to old password");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Errors while verifying password change:", errors);
        }
    }
}
