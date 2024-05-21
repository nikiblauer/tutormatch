package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSubjectDto extends SubjectDto {
    private String role;
}
