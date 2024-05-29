package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateChatRoomDto;

import java.util.List;

public interface ChatRoomService {
    List<ChatRoomDto> getChatRoomsByUserId(Long userId);

    ChatRoomDto createChatRoom(CreateChatRoomDto toCreate);

}
