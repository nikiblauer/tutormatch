package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WebSocketErrorDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.InvalidMessageException;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.util.Map;


@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageService chatMessageService;
    private final UserService userService;


    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.userService = userService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDto chatMessageDto, Principal principal) throws InvalidMessageException {
        // Check if sender in dto matches the actual sender in token
        String authenticatedUsername = principal.getName();
        Long authenticatedUserId = userService.findApplicationUserByEmail(authenticatedUsername).getId();
        ApplicationUser dtoUser = userService.findApplicationUserById(chatMessageDto.getSenderId());

        if (!authenticatedUsername.equals(dtoUser.getDetails().getEmail())) { // Assuming you have a senderUsername field in ChatMessageDto
            LOGGER.warn("Sender does not match authenticated user.");
            throw new InvalidMessageException("Sender does not match authenticated user.", authenticatedUserId);
        }


        // Persist message in database
        if (!chatMessageService.saveChatMessage(chatMessageDto)) {
            LOGGER.warn("Chat messages is malformed.");
            throw new InvalidMessageException("Chat messages is malformed.", authenticatedUserId);
        }

        // This sends the received message to the specified recipient
        messagingTemplate.convertAndSendToUser(chatMessageDto.getRecipientId().toString(), "/queue/messages", chatMessageDto);
    }

    @MessageExceptionHandler(InvalidMessageException.class)
    public void handleInvalidMessageException(InvalidMessageException exception) {
        WebSocketErrorDto errorDto = new WebSocketErrorDto(exception.getMessage());
        messagingTemplate.convertAndSendToUser(exception.getUserId().toString(), "/queue/errors", errorDto);
    }

}