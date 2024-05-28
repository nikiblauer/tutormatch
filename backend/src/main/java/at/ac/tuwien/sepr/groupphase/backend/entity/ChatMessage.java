package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="CHATROOM_ID")
    private ChatRoom chatRoomId;
    @ManyToOne
    @JoinColumn(name="SENDER_ID")
    private ApplicationUser senderId;
    @ManyToOne
    @JoinColumn(name="RECIPIENT_ID")
    private ApplicationUser recipientId;
    private String content;
    private Date timestamp;
}