package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatRoomRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ChatRoomService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ChatRoomServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile("generateData")
@Component
@DependsOn({"userDataGenerator"})
public class ChatRoomDataGenerator {

    private ChatRoomRepository chatRoomRepository;
    private UserRepository userRepository;
    private ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomDataGenerator(ChatRoomRepository chatRoomRepository, UserRepository userRepository, ChatRoomService chatRoomService) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.chatRoomService = chatRoomService;
    }


    @PostConstruct
    public void generateChatRooms() throws ValidationException {
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

        if (verifiedUsers.size() < 2) {
            log.info("Not enough verified users to create chats.");
            return;
        }

        for (int i = 0; i < verifiedUsers.size(); i++) {
            ApplicationUser user1 = verifiedUsers.get(i);
            ApplicationUser user2 = verifiedUsers.get((i + 1) % verifiedUsers.size());
            CreateChatRoomDto receipient = new CreateChatRoomDto();
            receipient.setRecipientId(user2.getId());
            chatRoomService.createChatRoom(user1, receipient);
        }

    }

}
