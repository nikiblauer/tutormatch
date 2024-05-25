package at.ac.tuwien.sepr.groupphase.backend.entity;


import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    @Id
    private String id;
    private String senderId;
    private String recipientId;
    private String content;
}
