package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.entity.ChatMessage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatNotification;
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

    public ChatController(SimpMessagingTemplate messagingTemplate/*, ChatMessageService chatMessageService*/) {
        this.messagingTemplate = messagingTemplate;
        //this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat")
    public void processMessage(
        @Payload ChatMessage chatMessage
    ) {
        ChatMessage savedMsg = chatMessage; //chatMessageService.save(chatMessage);
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
        return null; //return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }
}