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
        ApplicationUser user = userService.findApplicationUserById(userId);
        var chatRooms = chatRoomRepository.findAllBySenderId(user);

        return chatRooms.stream()
            .map(chatRoom -> new ChatRoomDto(chatRoom.getId(), chatRoom.getChatId(), chatRoom.getSenderId().getId(), chatRoom.getRecipientId().getId()))
            .collect(Collectors.toList());
    }

    public ChatRoom getChatRoomById(Long id) {
        LOGGER.trace("getChatRoomId({})", id);
        return (ChatRoom) chatRoomRepository.findAllByChatId(id).get(0);
    }

    public Long createChatRoom(CreateChatRoomDto toCreate) {
        LOGGER.trace("createChatRoom({}, {})", toCreate.getSenderId(), toCreate.getRecipientId());

        Long senderId = toCreate.getSenderId();
        Long recipientId = toCreate.getRecipientId();

        Long chatId = (long) (senderId.toString() + recipientId.toString()).hashCode();

        ApplicationUser sender = userService.findApplicationUserById(senderId);
        ApplicationUser recipient = userService.findApplicationUserById(recipientId);
        ChatRoom senderRecipient = ChatRoom.builder().chatId(chatId).senderId(sender).recipientId(recipient).build();
        ChatRoom recipientSender = ChatRoom.builder().chatId(chatId).senderId(recipient).recipientId(sender).build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;
    }
}

