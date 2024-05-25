package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.entity.ChatMessage;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    @Autowired
    private ChatMessageService chatMessageService;

    @GetMapping("/{sender}/{receiver}")
    public List<ChatMessage> getChatMessages(@PathVariable String sender, @PathVariable String receiver) {
        return chatMessageService.getChatMessages(sender, receiver);
    }
}