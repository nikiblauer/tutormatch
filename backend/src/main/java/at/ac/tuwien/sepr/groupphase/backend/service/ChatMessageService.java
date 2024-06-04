package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessageDto> getChatMessagesByChatRoomId(String chatRoomId);

    void saveChatMessage(ChatMessageDto chatMessageDto);
}
