package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatRoomRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ChatRoomService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ChatRoomRepository chatRoomRepository;

    private final UserService userService;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository, UserService userService) {
        this.chatRoomRepository = chatRoomRepository;
        this.userService = userService;
    }

    public List<ChatRoomDto> getChatRoomsByUserId(Long userId) {
        LOGGER.trace("getChatRoomByUserId({})", userId);

        // get all chat room for userId
        var chatRooms = chatRoomRepository.findAllBySenderId(userId);

        // map to ChatRoomDto
        return chatRooms.stream()
            .map(chatRoom -> new ChatRoomDto(chatRoom.getId(), chatRoom.getChatId(), chatRoom.getSender().getId(), chatRoom.getRecipient().getId()))
            .collect(Collectors.toList());
    }

    public ChatRoomDto createChatRoom(CreateChatRoomDto toCreate) {
        LOGGER.trace("createChatRoom({}, {})", toCreate.getSenderId(), toCreate.getRecipientId());

        String chatId = UUID.randomUUID().toString();

        ApplicationUser sender = userService.findApplicationUserById(toCreate.getSenderId());
        ApplicationUser recipient = userService.findApplicationUserById(toCreate.getRecipientId());
        ChatRoom senderRecipient = ChatRoom.builder().chatId(chatId).sender(sender).recipient(recipient).build();
        ChatRoom recipientSender = ChatRoom.builder().chatId(chatId).sender(recipient).recipient(sender).build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return new ChatRoomDto(senderRecipient.getId(), senderRecipient.getChatId(), senderRecipient.getSender().getId(), senderRecipient.getRecipient().getId());
    }
}

