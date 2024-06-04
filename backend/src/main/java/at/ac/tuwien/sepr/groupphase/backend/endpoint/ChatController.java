package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageService chatMessageService;


    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDto chatMessageDto) {
        // Persist message in database
        chatMessageService.saveChatMessage(chatMessageDto);

        // This sends the received message to the specified recipient
        messagingTemplate.convertAndSendToUser(chatMessageDto.getRecipientId().toString(), "/queue/messages", chatMessageDto);
    }
}