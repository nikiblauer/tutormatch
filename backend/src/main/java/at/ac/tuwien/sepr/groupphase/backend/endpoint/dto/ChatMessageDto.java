package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    @NotNull
    private String chatRoomId;
    @NotNull
    private Long senderId;
    @NotNull
    private Long recipientId;
    @NotNull
    private String content;
    @NotNull
    private Date timestamp;
}