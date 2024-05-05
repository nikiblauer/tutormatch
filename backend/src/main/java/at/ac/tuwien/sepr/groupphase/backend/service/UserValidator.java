package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserValidator {
    private static final String VALIDATION_PATTERN_1 = "^[a-zA-Z0-9.+-]+@student.tuwien.ac.at";
    private static final String VALIDATION_PATTERN_2 = "^[a-zA-Z0-9.+-]+@tuwien.ac.at";

    public boolean validate(String email) throws ValidationException {
        return email.matches(VALIDATION_PATTERN_1) || email.matches(VALIDATION_PATTERN_2);
    }
    public void verifyUserData(ApplicationUserDto user) throws Exception {
        List<String> errors = new ArrayList<>();
        if (!validate(user.email)){
            errors.add("Email not valid, it has to end in tuwien.ac.at or student.tuwien.ac.at");
        }
        if (user.email.length() > 255) {
            errors.add("Email is too long");
        }
        if (user.name.isEmpty()) {
            errors.add("Name cannot be null");
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
        if (user.name.equals(" ")) {
            errors.add("Name cannot be whitespace");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
    }
}
