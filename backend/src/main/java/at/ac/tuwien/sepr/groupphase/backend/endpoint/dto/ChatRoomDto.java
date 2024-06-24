package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {
    private Long id;
    @NotNull
    private String chatRoomId;
    @NotNull
    private Long senderId;
    @NotNull
    private Long recipientId;
    private String senderFirstName;
    private String senderLastName;
    private String recipientFirstName;
    private String recipientLastName;

}