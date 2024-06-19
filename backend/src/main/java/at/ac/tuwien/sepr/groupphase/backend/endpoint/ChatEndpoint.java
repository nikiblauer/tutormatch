package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatRoomService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@Tag(name = "Chat Endpoint")
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

    @Operation(
        description = "Gets all chatrooms for user with specified id.",
        summary = "Gets all chatrooms for user(id)")
    @Secured("ROLE_ADMIN")
    @GetMapping("/room/user/{userId}")
    public List<ChatRoomDto> getChatRoomsByUserId(@PathVariable(name = "userId") Long userId) {
        LOGGER.info("GET /api/v1/chat/room/user/{}", userId);
        return chatRoomService.getChatRoomsByUserId(userId);
    }

    @Operation(
        description = "Gets all chatrooms for user, identified by the token.",
        summary = "Gets all chatrooms for user(token)")
    @Secured("ROLE_USER")
    @GetMapping("/room/user")
    public List<ChatRoomDto> getChatRooms() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("GET /api/v1/chat/room/user/ with email: {}", userEmail);
        ApplicationUser user = userService.findApplicationUserByEmail(userEmail);
        return chatRoomService.getChatRoomsByUserId(user.getId());
    }

    @Operation(
        description = "Creates a new chatroom for user identified by token and recipient specified in dto.",
        summary = "Creates a new chatroom")
    @Secured("ROLE_USER")
    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatRoomDto createChatRoom(@RequestBody CreateChatRoomDto chatRoomCreateDto) throws ValidationException {
        LOGGER.info("POST /api/v1/chat/room/create body: {}", chatRoomCreateDto);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userService.findApplicationUserByEmail(userEmail);
        return chatRoomService.createChatRoom(user, chatRoomCreateDto);
    }

    @Secured("ROLE_USER")
    @GetMapping("/room/recipient/{id}")
    public Boolean checkChatRoomExistsByRecipient(@PathVariable(name = "id") Long recipientId) {
        LOGGER.info("GET /api/v1/chat/room/recipient body: {}", recipientId);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser sender = userService.findApplicationUserByEmail(userEmail);
        return chatRoomService.checkChatRoomExistsByRecipient(sender.getId(), recipientId);
    }

    @Operation(
        description = "Gets all messages of a chatroom with the specified id.",
        summary = "Get all messages of chatroom")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/room/{chatRoomId}/messages")
    public List<ChatMessageDto> getMessagesByChatRoomId(@PathVariable(name = "chatRoomId") String chatRoomId) {
        LOGGER.info("GET /api/v1/chat/room/{}", chatRoomId);
        return chatMessageService.getChatMessagesByChatRoomId(chatRoomId);
    }

    @Operation(
        description = "Blocks a user for the user identified by the id",
        summary = "Block a user")
    @Secured("ROLE_USER")
    @PostMapping("/block/{userIdToBlock}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockUser(@PathVariable(name = "userIdToBlock") Long userIdToBlock) {
        LOGGER.info("POST /api/v1/chat/block/{}", userIdToBlock);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userService.findApplicationUserByEmail(userEmail);
        userService.blockUser(user.getId(), userIdToBlock);
    }

    @Operation(
        description = "Unblocks a user for the user identified by the id",
        summary = "Unblock a user")
    @Secured("ROLE_USER")
    @DeleteMapping("/unblock/{userIdToUnblock}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblockUser(@PathVariable(name = "userIdToUnblock") Long userIdToUnblock) {
        LOGGER.info("DELETE /api/v1/chat/unblock/{}", userIdToUnblock);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userService.findApplicationUserByEmail(userEmail);
        userService.unblockUser(user.getId(), userIdToUnblock);
    }

    @Operation(
        description = "Gets a list of all user IDs that the user with the given ID has blocked",
        summary = "Get blocked users")
    @Secured("ROLE_USER")
    @GetMapping("/block/{userId}")
    public List<Long> getBlockedUsers(@PathVariable("userId") Long userId) {
        LOGGER.info("GET /api/v1/chat/block/{}", userId);
        return userService.getBlockedUsers(userId);
    }
}
