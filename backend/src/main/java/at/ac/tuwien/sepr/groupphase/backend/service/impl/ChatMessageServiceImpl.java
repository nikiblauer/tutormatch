
package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatMessage;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatMessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatMessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatRoomService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private final ChatMessageRepository chatMessageRepository;
    private final CustomUserDetailService userService;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository, CustomUserDetailService userService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userService = userService;
    }

    public List<ChatMessageDto> getChatMessagesByChatRoomId(String chatRoomId) {
        LOGGER.trace("getChatMessagesByChatRoomId({})", chatRoomId);
        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomId(chatRoomId);

        return messages.stream()
            .map(message -> new ChatMessageDto(
                message.getId(),
                message.getChatRoomId(),
                message.getSenderId().getId(),
                message.getRecipientId().getId(),
                message.getContent(),
                message.getTimestamp()))
            .collect(Collectors.toList());
    }

    public void saveChatMessage(ChatMessageDto chatMessageDto) {
        LOGGER.trace("saveChatMessage({})", chatMessageDto);

        ChatMessage chatMessage = ChatMessage.builder()
            .chatRoomId(chatMessageDto.getChatRoomId())
            .senderId(userService.findApplicationUserById(chatMessageDto.getSenderId()))
            .recipientId(userService.findApplicationUserById(chatMessageDto.getRecipientId()))
            .content(chatMessageDto.getContent())
            .timestamp(chatMessageDto.getTimestamp())
            .build();

        chatMessageRepository.save(chatMessage);
    }
}
