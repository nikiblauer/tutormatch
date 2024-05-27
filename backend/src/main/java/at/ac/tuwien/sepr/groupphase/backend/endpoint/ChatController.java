package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.entity.ChatMessage;
//import at.ac.tuwien.sepr.groupphase.backend.entity.ChatNotification;
//import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    //private final ChatMessageService chatMessageService;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void processMessage(
        @Payload ChatMessage chatMessage
    ) {
        ChatMessage savedMsg = chatMessage; //here should be something like chatMessageService.save(chatMessage);

        // This sends the received message to the specified recipient
        messagingTemplate.convertAndSendToUser(
            chatMessage.getRecipientId().toString(), "/queue/messages",
            ChatMessage.builder()
                .id(savedMsg.getId())
                .senderId(savedMsg.getSenderId())
                .recipientId(savedMsg.getRecipientId())
                .timestamp(savedMsg.getTimestamp())
                .content(savedMsg.getContent())
                .build()
        );
    }
}