package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface ChatRoomService {
    /**
     * This method is used to retrieve all chatrooms the specified user {@code userId} is participating in.
     *
     * @param userId user for which to retrieve all chatrooms
     * @return list of all chatrooms the user is participant
     */
    List<ChatRoomDto> getChatRoomsByUserId(Long userId);

    /**
     * Creates a new chatroom for sender and toCreate.getRecipient()
     *
     * @param sender   user who requested to create new chatroom
     * @param toCreate dto containing the other chatroom participant
     * @return ChatRoomDto from creators perspective
     */
    ChatRoomDto createChatRoom(ApplicationUser sender, CreateChatRoomDto toCreate) throws ValidationException;

    /**
     * Retrieves a chatroom by chatroom id.
     *
     * @param chatRoomId the room to retrieve
     * @return chatroom dto
     */
    ChatRoomDto getChatRoomByChatRoomId(String chatRoomId);

    Boolean checkChatRoomExistsByRecipient(Long senderId, Long recipientId);
}
