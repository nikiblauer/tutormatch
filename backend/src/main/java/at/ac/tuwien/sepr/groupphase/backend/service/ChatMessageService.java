package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;

import java.util.List;

public interface ChatMessageService {
    /**
     * Retrieving all chat messages for the specified chatroom id
     * @param chatRoomId chatroom to get the messages from
     * @return list of all chat messages in this chatroom
     */
    List<ChatMessageDto> getChatMessagesByChatRoomId(String chatRoomId);

    /**
     * This function persists the specified chat messages in the persistent data store
     * @param chatMessageDto message to persist
     */
    void saveChatMessage(ChatMessageDto chatMessageDto);
}
