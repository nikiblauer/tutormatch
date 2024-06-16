package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatMessage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatMessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatRoomRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Profile("generateData")
@Component
@DependsOn({"userDataGenerator", "chatRoomDataGenerator"})
public class ChatMessageDataGenerator {

    private ChatMessageRepository chatMessageRepository;
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatMessageDataGenerator(ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    @PostConstruct
    public void generateChatMessages() {
        if (chatMessageRepository.existsById(1L)) {
            log.info("ChatMessage data already generated. Skipping generation.");
            return;
        }
        log.info("Generating chat messages...");

        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        int i = 0;
        for (ChatRoom chatRoom : chatRooms) {
            if (i % 2 == 0) {
                i++;
                continue;
            }
            ApplicationUser user1 = chatRoom.getSender();
            ApplicationUser user2 = chatRoom.getRecipient();

            LocalDateTime dateTime1 = LocalDateTime.of(2023, 1, 1, 0, 0);
            LocalDateTime dateTime2 = dateTime1.plusMinutes(1);
            Date timestampMsg1 = Date.from(dateTime1.atZone(ZoneId.systemDefault()).toInstant());
            Date timestampMsg2 = Date.from(dateTime2.atZone(ZoneId.systemDefault()).toInstant());

            ChatMessage chatMessage1 = ChatMessage.builder().chatRoomId(chatRoom.getChatRoomId()).senderId(user1).recipientId(user2)
                .content("Hi " + user2.getFirstname() + ", how are you?") // Customizing message content
                .timestamp(timestampMsg1).build();

            ChatMessage chatMessage2 = ChatMessage.builder().chatRoomId(chatRoom.getChatRoomId()).senderId(user2).recipientId(user1)
                .content("Hi " + user1.getFirstname() + ", I'm fine. How are you?").timestamp(timestampMsg2).build();

            chatMessageRepository.save(chatMessage1);
            chatMessageRepository.save(chatMessage2);
            i++;
        }
        log.info("ChatMessage generation completed.");
    }
}