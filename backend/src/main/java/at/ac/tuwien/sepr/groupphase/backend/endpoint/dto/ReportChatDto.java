package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportChatDto {
    String chatId;
    @Size(max = 100, message = "reason too long")
    String reason;
}
