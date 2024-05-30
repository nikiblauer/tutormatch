package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private String chatRoomId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private Date timestamp;
}