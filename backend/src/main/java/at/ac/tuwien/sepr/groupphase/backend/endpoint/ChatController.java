package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WebSocketErrorDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserBlock;
import at.ac.tuwien.sepr.groupphase.backend.exception.InvalidMessageException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserBlockRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.util.Optional;


@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageService chatMessageService;
    private final UserBlockRepository userBlockRepository;
    private final UserService userService;


    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService, UserService userService, UserBlockRepository userBlockRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.userService = userService;
        this.userBlockRepository = userBlockRepository;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDto chatMessageDto, Principal principal) throws InvalidMessageException {
        // Check if sender in dto matches the actual sender in token
        String authenticatedUsername = principal.getName();
        Long authenticatedUserId = userService.findApplicationUserByEmail(authenticatedUsername).getId();
        ApplicationUser dtoUser = userService.findApplicationUserById(chatMessageDto.getSenderId());

        if (!authenticatedUsername.equals(dtoUser.getDetails().getEmail())) {
            LOGGER.warn("Sender does not match authenticated user.");
            throw new InvalidMessageException("Sender does not match authenticated user.", authenticatedUserId);
        }

        // Check if sender has blocked recipient or vice versa
        Optional<UserBlock> senderBlockOptional = userBlockRepository.findByUserAndBlockedUser(chatMessageDto.getSenderId(), chatMessageDto.getRecipientId());
        Optional<UserBlock> recipientBlockOptional = userBlockRepository.findByUserAndBlockedUser(chatMessageDto.getRecipientId(), chatMessageDto.getSenderId());
        if (senderBlockOptional.isPresent() || recipientBlockOptional.isPresent()) {
            LOGGER.warn("Chat messages is blocked.");
            throw new InvalidMessageException("Recipient has blocked you/or you have blocked recipient.", authenticatedUserId);
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