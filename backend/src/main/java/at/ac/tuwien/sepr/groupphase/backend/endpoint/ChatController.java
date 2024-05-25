package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.entity.ChatMessage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatNotification;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    public void processMessage(
        @Payload ChatMessage chatMessage
    ) {
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
            chatMessage.getRecipientId(), "/queue/messages",
            ChatNotification.builder()
                .id(savedMsg.getId())
                .senderId(savedMsg.getSenderId())
                .recipientId(savedMsg.getRecipientId())
                .content(savedMsg.getContent())
                .build()
        );
    }


    @GetMapping("/chatMessages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>>findChatMessages(
        @PathVariable("senderId") String senderId,
        @PathVariable("recipientId") String recipientId
    ) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }
}