/*
package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.ChatMessage;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;

    public ChatMessageService(ChatMessageRepository repository, ChatRoomService chatRoomService) {
        this.repository = repository;
        this.chatRoomService = chatRoomService;
    }

    public ChatMessage save(ChatMessage chatMessage){
        var chatId = chatRoomService.getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
            .orElseThrow();

        chatMessage.setChatId(chatId);
        //repository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId){
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);

        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }
}*/
