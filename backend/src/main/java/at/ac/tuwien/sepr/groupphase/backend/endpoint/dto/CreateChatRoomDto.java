package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRoomDto {
    private Long recipientId;
}