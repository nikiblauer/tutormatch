package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserValidator {
    private static final String VALIDATION_PATTERN_1 = "^[a-zA-Z0-9.+-]+@student\\.tuwien\\.ac\\.at$";
    private static final String VALIDATION_PATTERN_2 = "^[a-zA-Z0-9.+-]+@tuwien\\.ac\\.at$";
    private static final String VALIDATION_PATTERN_3 = "^\\s+";
    private static final String VALIDATION_PATTERN_4 = "^(?:\\+?\\d⋅?){6,14}\\d$";


    public boolean validate(String email) {
        return email.matches(VALIDATION_PATTERN_1) || email.matches(VALIDATION_PATTERN_2);
    }

    public void verifyUserData(ApplicationUserDto user) throws Exception {
        List<String> errors = new ArrayList<>();
        if (user.email.isEmpty()) {
            errors.add("Email cannot be empty");
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
