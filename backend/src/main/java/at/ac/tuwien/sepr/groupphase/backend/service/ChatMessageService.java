
package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatMessage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatRoomService chatRoomService) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomService = chatRoomService;
    }

    public ChatMessage save(ChatMessage chatMessage){
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessageDto> getChatMessagesByChatRoomId(Long chatRoomId){
        ChatRoom chatRoom = chatRoomService.getChatRoomById(chatRoomId);
        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomId(chatRoom);

        return messages.stream()
            .map(message -> new ChatMessageDto(
                message.getId(),
                message.getChatRoomId().getChatId(),
                message.getSenderId().getId(),
                message.getRecipientId().getId(),
                message.getContent(),
                message.getTimestamp()))
            .collect(Collectors.toList());
    }
}
