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
    private long matrNumber;
    private String firstname;
    private String lastname;
    private String email;
    private String telNr;
    private long traineeMatchingcount;
    private long tutorMatchingcount;
    private long totalMatchingcount;
    private String subjectTitles;
}
