package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserMatchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatRoomService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserMatchService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class ChatValidator {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserMatchService userMatchService;

    public ChatValidator(UserMatchService userMatchService) {
        this.userMatchService = userMatchService;
    }


    public ArrayList<String> validateForCreate(ApplicationUser sender, ApplicationUser recipient) throws ValidationException {
        LOGGER.trace("validateForCreate({}, {})", sender, recipient);
        ArrayList<String> errors = new ArrayList<>();

        if (recipient.getId().equals(sender.getId())) {
            errors.add("Sender cannot also be recipient.");
        }

        if (userMatchService.findMatchingsForUser(sender.getDetails().getEmail()).noneMatch(userMatchDto -> recipient.getId().equals(userMatchDto.getId()))) {
            errors.add("Sender has no match with recipient.");
        }

        return errors;
    }
}
