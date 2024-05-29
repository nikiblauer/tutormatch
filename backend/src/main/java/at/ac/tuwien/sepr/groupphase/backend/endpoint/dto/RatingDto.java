package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class RatingDto {
    public float rating;
    public Long rated;
}
