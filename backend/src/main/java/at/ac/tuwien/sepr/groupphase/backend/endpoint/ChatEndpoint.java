package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatRoomService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/chat")
public class ChatEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    private final UserService userService;

    @Autowired
    public ChatEndpoint(ChatRoomService chatRoomService, ChatMessageService chatMessageService, UserService userService) {
        this.chatRoomService = chatRoomService;
        this.chatMessageService = chatMessageService;
        this.userService = userService;
    }


    // for admin (later for reviewing chat reports)
    @Secured("ROLE_ADMIN")
    @GetMapping("/room/user/{userId}")
    public List<ChatRoomDto> getChatRoomsByUserId(@PathVariable(name = "userId") Long userId) {
        LOGGER.info("GET /api/v1/chat/room/user/{}", userId);

        return chatRoomService.getChatRoomsByUserId(userId);
    }

    @Secured("ROLE_USER")
    @GetMapping("/room/user")
    public List<ChatRoomDto> getChatRooms() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        LOGGER.info("GET /api/v1/chat/room/user/ with email: {}", userEmail);

        ApplicationUser user = userService.findApplicationUserByEmail(userEmail);

        return chatRoomService.getChatRoomsByUserId(user.getId());
    }



    @Secured("ROLE_USER")
    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatRoomDto createChatRoom(@RequestBody CreateChatRoomDto chatRoomCreateDto) {
        LOGGER.info("POST /api/v1/chat/room/create body: {}", chatRoomCreateDto);

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userService.findApplicationUserByEmail(userEmail);


        return chatRoomService.createChatRoom(user, chatRoomCreateDto);
    }

    // Also access for admin -> Later used for reviewing chat reports
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/room/{chatRoomId}/messages")
    public List<ChatMessageDto> getMessagesByChatRoomId(@PathVariable(name = "chatRoomId") String chatRoomId) {
        LOGGER.info("GET /api/v1/chat/room/{}", chatRoomId);

        return chatMessageService.getChatMessagesByChatRoomId(chatRoomId);
    }
}
