package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserValidator {
    public void verifyUserData(ApplicationUserDto user) throws Exception {
        List<String> errors = new ArrayList<>();
        if (!user.email.endsWith("tuwien.ac.at")) {
            errors.add("Email has to end in tuwien.ac.at");
        }
        if (user.email.length() > 255) {
            errors.add("Email is too long");
        }
        if (user.name.isEmpty()) {
            errors.add("Name cannot be null");
        }
        if (user.name.equals(" ")) {
            errors.add("Name cannot be whitespace");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Errors while verifying user Data:", errors);
        }
    }
}
