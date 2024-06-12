package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import org.mapstruct.Mapper;

@Mapper
public interface ChatRoomMapper {
    default ChatRoomDto chatRoomToDto(ChatRoom chatRoom) {
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getChatRoomId(), chatRoom.getSender().getId(), chatRoom.getRecipient().getId(),
            chatRoom.getSender().getFirstname(), chatRoom.getSender().getLastname(),
            chatRoom.getRecipient().getFirstname(), chatRoom.getRecipient().getLastname());
    }
}
