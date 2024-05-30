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
import java.util.Date;

@Slf4j
@Profile("generateData")
@Component
@DependsOn({"userDataGenerator", "chatRoomDataGenerator"})
public class ChatMessageDataGenerator {

    private ChatMessageRepository chatMessageRepository;
    private UserRepository userRepository;

    @Autowired
    public ChatMessageDataGenerator(ChatMessageRepository chatRoomRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }


    @PostConstruct
    public void generateChatMessages() {
        if (chatMessageRepository.existsById(1L)) {
            log.info("ChatMessage data already generated. Skipping generation.");
            return;
        }

        log.info("Generating chat messages...");

        ApplicationUser user1 = userRepository.findApplicationUserByDetails_Email("e10000001@student.tuwien.ac.at");
        ApplicationUser user3 = userRepository.findApplicationUserByDetails_Email("e10000003@student.tuwien.ac.at");



        // hardcoded because UUID generates with time specific seed
        String chatRoomId = "123e4567-e89b-12d3-a456-426614174000";

        LocalDate localDate = LocalDate.of(2023, 1, 1);
        System.out.println("LocalDate: " + localDate);

        // Convert LocalDate to Date
        LocalDateTime dateTime1 = LocalDateTime.of(2023, 1, 1, 0, 0);

        // Add 1 minute to the first date to get the second date
        LocalDateTime dateTime2 = dateTime1.plusMinutes(1);

        // Convert LocalDateTime to Date
        Date timestampMsg1 = Date.from(dateTime1.atZone(ZoneId.systemDefault()).toInstant());
        Date timestampMsg2 = Date.from(dateTime2.atZone(ZoneId.systemDefault()).toInstant());


        ChatMessage chatMessage1 = ChatMessage.builder()
            .chatRoomId(chatRoomId)
            .senderId(user1)
            .recipientId(user3)
            .content("Hi, how are you?")
            .timestamp(timestampMsg1)
            .build();

        ChatMessage chatMessage2 = ChatMessage.builder()
            .chatRoomId(chatRoomId)
            .senderId(user3)
            .recipientId(user1)
            .content("I'm fine. How are you?")
            .timestamp(timestampMsg2)
            .build();

        chatMessageRepository.save(chatMessage1);
        chatMessageRepository.save(chatMessage2);



        log.info("ChatMessage generation completed.");
    }

}
