package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChatRoomMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatRoomRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatRoomService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.ChatValidator;
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

    private final ChatRoomMapper chatRoomMapper;

    private final ChatValidator chatValidator;

    @Autowired
    public ChatRoomServiceImpl(ChatRoomRepository chatRoomRepository, UserService userService, ChatRoomMapper chatRoomMapper, ChatValidator chatValidator) {
        this.chatRoomRepository = chatRoomRepository;
        this.userService = userService;
        this.chatRoomMapper = chatRoomMapper;
        this.chatValidator = chatValidator;
    }

    public List<ChatRoomDto> getChatRoomsByUserId(Long userId) {
        LOGGER.trace("getChatRoomByUserId({})", userId);

        var chatRooms = chatRoomRepository.findAllBySenderId(userId);

        return chatRooms.stream()
            .map(chatRoomMapper::chatRoomToDto)
            .collect(Collectors.toList());
    }

    public ChatRoomDto createChatRoom(ApplicationUser sender, CreateChatRoomDto toCreate) throws ValidationException {
        LOGGER.trace("createChatRoom({}, {})", sender.getId(), toCreate.getRecipientId());

        String chatRoomId = UUID.randomUUID().toString();

        ApplicationUser recipient = userService.findApplicationUserById(toCreate.getRecipientId());

        chatValidator.validateForCreate(sender, recipient);


        ChatRoom senderRecipient = ChatRoom.builder().chatRoomId(chatRoomId).sender(sender).recipient(recipient).build();
        ChatRoom recipientSender = ChatRoom.builder().chatRoomId(chatRoomId).sender(recipient).recipient(sender).build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatRoomMapper.chatRoomToDto(senderRecipient);
    }

    @Override
    public ChatRoomDto getChatRoomByChatRoomId(String chatRoomId) {
        LOGGER.trace("getChatRoomByChatRoomId({})", chatRoomId);

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByChatRoomId(chatRoomId);

        if (chatRooms.isEmpty()) {
            throw new NotFoundException("ChatRoom with this id was not found!");
        }

        return chatRoomMapper.chatRoomToDto(chatRooms.getFirst());
    }

    @Override
    public Boolean checkChatRoomExistsByRecipient(Long senderId, Long recipientId) {
        ChatRoom chatroom = chatRoomRepository.findChatRoomBySenderIdAndRecipientId(senderId, recipientId);
        return chatroom != null;
    }
}

