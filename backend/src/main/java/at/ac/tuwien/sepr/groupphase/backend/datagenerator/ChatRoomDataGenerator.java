package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatRoomRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UserMatchService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Profile("generateData")
@Component
@DependsOn({"userDataGenerator", "userSubjectDataGenerator"})
public class ChatRoomDataGenerator {

    private ChatRoomRepository chatRoomRepository;
    private UserRepository userRepository;
    private UserMatchService userMatchService;

    @Autowired
    public ChatRoomDataGenerator(ChatRoomRepository chatRoomRepository, UserRepository userRepository, UserMatchService userMatchService) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.userMatchService = userMatchService;
    }


    @PostConstruct
    public void generateChatRooms() {
        if (chatRoomRepository.existsById(1L)) {
            log.info("ChatRoom data already generated. Skipping generation.");
            return;
        }

        log.info("Generating chatrooms...");
        List<ApplicationUser> users = userRepository.findAll();
        ArrayList<ApplicationUser> verifiedUsers = new ArrayList<>();

        for (ApplicationUser user : users) {
            if (user.getVerified() && !(user.getAdmin())) {
                verifiedUsers.add(user);
            }
        }


        for (int i = 0; i < verifiedUsers.size(); i++) {
            ApplicationUser user1 = verifiedUsers.get(i);
            ApplicationUser user2 = verifiedUsers.get((i + 1) % verifiedUsers.size());

            if (userMatchService.findMatchingsForUser(user1.getDetails().getEmail()).anyMatch(userMatchDto -> user2.getId().equals(userMatchDto.getId()))) {
                String chatRoomId = UUID.randomUUID().toString();
                ChatRoom senderRecipient = ChatRoom.builder().chatRoomId(chatRoomId).sender(user1).recipient(user2).build();
                ChatRoom recipientSender = ChatRoom.builder().chatRoomId(chatRoomId).sender(user2).recipient(user1).build();

                chatRoomRepository.save(senderRecipient);
                chatRoomRepository.save(recipientSender);

            }

        }

    }

}
