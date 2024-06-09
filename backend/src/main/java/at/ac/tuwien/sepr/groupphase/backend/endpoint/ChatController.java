package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
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
    public void processMessage(@Payload ChatMessageDto chatMessageDto, Principal principal) {
        // Check if sender in dto matches the actual sender in token
        String authenticatedUsername = principal.getName();
        ApplicationUser dtoUser = userService.findApplicationUserById(chatMessageDto.getSenderId());

        // websocket doesn't support error codes, so there is no error sent back. Message is just not persisted if malformed
        if (!authenticatedUsername.equals(dtoUser.getDetails().getEmail())) { // Assuming you have a senderUsername field in ChatMessageDto
            LOGGER.warn("Sender does not match authenticated user.");
            return;
        }


        // Persist message in database
        // websocket doesn't support error codes, so there is no error sent back. Message is just not persisted if malformed
        if(!chatMessageService.saveChatMessage(chatMessageDto)){
            LOGGER.warn("Chat messages is malformed.");
            return;
        }

        // This sends the received message to the specified recipient
        messagingTemplate.convertAndSendToUser(chatMessageDto.getRecipientId().toString(), "/queue/messages", chatMessageDto);
    }
}