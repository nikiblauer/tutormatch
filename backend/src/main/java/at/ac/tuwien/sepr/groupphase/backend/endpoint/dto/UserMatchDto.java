package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMatchDto {
    private long id;
    private String firstname;
    private String lastname;
    private long traineeMatchingcount;
    private long tutorMatchingcount;
    private long totalMatchingcount;
    private String traineeSubjects;
    private String tutorSubjects;
    private float rating;
    private long amount;
}
