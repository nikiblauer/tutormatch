package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackCreateDto {
    public String feedback;
    public Long rated;
}
