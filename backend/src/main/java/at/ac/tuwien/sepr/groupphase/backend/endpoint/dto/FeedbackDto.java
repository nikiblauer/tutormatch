package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackDto extends FeedbackCreateDto {
    long id;
    long rating;

}
