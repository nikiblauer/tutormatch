package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collector;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {
    private Long id;
    private String chatRoomId;
    private Long senderId;
    private Long recipientId;
    private String senderFirstName;
    private String senderLastName;
    private String recipientFirstName;
    private String recipientLastName;

}