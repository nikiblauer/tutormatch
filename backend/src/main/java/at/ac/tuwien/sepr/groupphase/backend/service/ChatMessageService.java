package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.ChatMessage;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> getChatMessages(String sender, String receiver) {
        return chatMessageRepository.findBySenderAndReceiver(sender, receiver);
    }

    public ChatMessage saveChatMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }
}