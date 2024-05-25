package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.entity.ChatMessage;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    public void send(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        chatMessageService.saveChatMessage(message);
        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/messages", message);
    }
}