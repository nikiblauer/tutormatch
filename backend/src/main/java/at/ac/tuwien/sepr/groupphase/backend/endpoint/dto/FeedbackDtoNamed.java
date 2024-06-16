package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackDtoNamed extends FeedbackDto {
    public String ratedName;
}
