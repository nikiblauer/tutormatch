package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllBySenderId(Long sender);

    @Query("SELECT id from ChatRoom where sender.id IN (:participant1, :participant2) and recipient.id IN (:participant1, :participant2)")
    List<Long> findAllBySenderAndReceiverId(@Param("participant1") Long participant1, @Param("participant2") Long participant2);

    List<ChatRoom> findAllByChatRoomId(String chatRoomId);

    ChatRoom findChatRoomBySenderIdAndRecipientId(Long sender, Long recipient);
}