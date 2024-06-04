package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatRoomRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("generateData")
@Component
@DependsOn({"userDataGenerator"})
public class ChatRoomDataGenerator {

    private ChatRoomRepository chatRoomRepository;
    private UserRepository userRepository;

    @Autowired
    public ChatRoomDataGenerator(ChatRoomRepository chatRoomRepository, UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }


    @PostConstruct
    public void generateChatRooms() {
        if (chatRoomRepository.existsById(1L)) {
            log.info("ChatRoom data already generated. Skipping generation.");
            return;
        }

        log.info("Generating chatrooms...");

        ApplicationUser user1 = userRepository.findApplicationUserByDetails_Email("e10000001@student.tuwien.ac.at");
        ApplicationUser user3 = userRepository.findApplicationUserByDetails_Email("e10000003@student.tuwien.ac.at");
        ApplicationUser user4 = userRepository.findApplicationUserByDetails_Email("e10000004@student.tuwien.ac.at");


        // hardcoded because UUID generates with time specific seed
        String chatRoomId1 = "123e4567-e89b-12d3-a456-426614174000";
        String chatRoomId2 = "321e4567-e89b-12d3-a456-426614174000";



        ChatRoom senderRecipient1 = ChatRoom.builder().chatRoomId(chatRoomId1).sender(user1).recipient(user3).build();
        ChatRoom recipientSender1 = ChatRoom.builder().chatRoomId(chatRoomId1).sender(user3).recipient(user1).build();

        chatRoomRepository.save(senderRecipient1);
        chatRoomRepository.save(recipientSender1);

        ChatRoom senderRecipient2 = ChatRoom.builder().chatRoomId(chatRoomId2).sender(user1).recipient(user4).build();
        ChatRoom recipientSender2 = ChatRoom.builder().chatRoomId(chatRoomId2).sender(user4).recipient(user1).build();

        chatRoomRepository.save(senderRecipient2);
        chatRoomRepository.save(recipientSender2);


        log.info("Chatrooms generation completed.");
    }

}
