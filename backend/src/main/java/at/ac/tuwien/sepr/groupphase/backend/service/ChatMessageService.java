
package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatMessage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatMessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    private final CustomUserDetailService userService;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatRoomService chatRoomService, CustomUserDetailService userService) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomService = chatRoomService;
        this.userService = userService;
    }

    public List<ChatMessageDto> getChatMessagesByChatRoomId(Long chatRoomId) {
        LOGGER.trace("getChatMessagesByChatRoomId({})", chatRoomId);

        ChatRoom chatRoom = chatRoomService.getChatRoomById(chatRoomId);
        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomId(chatRoom);

        return messages.stream()
            .map(message -> new ChatMessageDto(
                message.getId(),
                message.getChatRoomId().getChatId(),
                message.getSenderId().getId(),
                message.getRecipientId().getId(),
                message.getContent(),
                message.getTimestamp()))
            .collect(Collectors.toList());
    }

    public ChatMessage saveChatMessage(ChatMessageDto chatMessageDto) {
        LOGGER.trace("saveChatMessage({})", chatMessageDto);

        ChatMessage chatMessage = ChatMessage.builder()
            .chatRoomId(chatRoomService.getChatRoomById(chatMessageDto.getChatRoomId()))
            .senderId(userService.findApplicationUserById(chatMessageDto.getSenderId()))
            .recipientId(userService.findApplicationUserById(chatMessageDto.getRecipientId()))
            .content(chatMessageDto.getContent())
            .timestamp(chatMessageDto.getTimestamp())
            .build();

        return chatMessageRepository.save(chatMessage);
    }
}
