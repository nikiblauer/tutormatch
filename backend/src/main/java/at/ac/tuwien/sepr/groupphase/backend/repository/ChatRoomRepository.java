package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllBySenderId(ApplicationUser sender);

    ChatRoom findOneByChatId(Long id);

    List<Object> findAllByChatId(Long id);

    //Optional<ChatRoom> findBySenderIdAndRecipientId(Long senderId, Long recipientId);
}