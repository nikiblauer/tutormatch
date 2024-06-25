package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackCreateDto {
    @Size(max = 500, message = "feedback too long")
    public String feedback;
    public Long rated;
}
