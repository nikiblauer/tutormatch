package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStudentAsAdminDto extends UpdateStudentDto {

    public Long matrNumber;

    public UpdateStudentAsAdminDto() {

    }
}
