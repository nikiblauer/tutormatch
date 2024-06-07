package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatRoomRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatRoomService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ChatRoomRepository chatRoomRepository;

    private final UserService userService;

    @Autowired
    public ChatRoomServiceImpl(ChatRoomRepository chatRoomRepository, UserService userService) {
        this.chatRoomRepository = chatRoomRepository;
        this.userService = userService;
    }

    public List<ChatRoomDto> getChatRoomsByUserId(Long userId) {
        LOGGER.trace("getChatRoomByUserId({})", userId);

        // get all chat room for userId
        var chatRooms = chatRoomRepository.findAllBySenderId(userId);

        // map to ChatRoomDto
        return chatRooms.stream()
            .map(chatRoom -> new ChatRoomDto(chatRoom.getId(), chatRoom.getChatRoomId(), chatRoom.getSender().getId(), chatRoom.getRecipient().getId(),
                chatRoom.getSender().getFirstname(), chatRoom.getSender().getLastname(),
                chatRoom.getRecipient().getFirstname(), chatRoom.getRecipient().getLastname()))
            .collect(Collectors.toList());
    }

    public ChatRoomDto createChatRoom(ApplicationUser sender, CreateChatRoomDto toCreate) {
        LOGGER.trace("createChatRoom({}, {})", sender.getId(), toCreate.getRecipientId());

        String chatRoomId = UUID.randomUUID().toString();

        ApplicationUser recipient = userService.findApplicationUserById(toCreate.getRecipientId());
        ChatRoom senderRecipient = ChatRoom.builder().chatRoomId(chatRoomId).sender(sender).recipient(recipient).build();
        ChatRoom recipientSender = ChatRoom.builder().chatRoomId(chatRoomId).sender(recipient).recipient(sender).build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return new ChatRoomDto(senderRecipient.getId(), senderRecipient.getChatRoomId(), senderRecipient.getSender().getId(),
            senderRecipient.getRecipient().getId(), senderRecipient.getSender().getFirstname(), senderRecipient.getSender().getLastname(),
            senderRecipient.getRecipient().getFirstname(), senderRecipient.getRecipient().getLastname());
    }
}

