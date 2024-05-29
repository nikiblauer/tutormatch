package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


import lombok.Data;

@Data
public class RatingDto {
    public float rating;
    public Long ratedUserid;
}
