package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatRoomService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/chat")
public class ChatEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @Autowired
    public ChatEndpoint(ChatRoomService chatRoomService, ChatMessageService chatMessageService) {
        this.chatRoomService = chatRoomService;
        this.chatMessageService = chatMessageService;
    }


    @PermitAll
    @GetMapping("/room/user/{userId}")
    public List<ChatRoomDto> getChatRoomsByUserId(@PathVariable(name = "userId") Long userId) {
        LOGGER.info("GET /api/v1/chat/room/user/{}", userId);
        List<ChatRoomDto> chatRooms = chatRoomService.getChatRoomsByUserId(userId);
        return chatRooms;
    }

    @PermitAll
    @PostMapping("room")
    public Long createChat(@RequestBody CreateChatRoomDto chatRoomCreateDto) {
        LOGGER.info("POST /api/v1/chat/room/create with senderId: {} and recipientId: {}",
            chatRoomCreateDto.getSenderId(), chatRoomCreateDto.getRecipientId());
        return chatRoomService.createChatRoom(chatRoomCreateDto.getSenderId(), chatRoomCreateDto.getRecipientId());
    }

    @PermitAll
    @GetMapping("/room/{chatRoomId}")
    public List<ChatMessageDto> getMessagesByChatRoomId(@PathVariable(name = "chatRoomId") Long chatRoomId) {
        LOGGER.info("GET /api/v1/chat/room/{}", chatRoomId);
        return chatMessageService.getChatMessagesByChatRoomId(chatRoomId);
    }
}
