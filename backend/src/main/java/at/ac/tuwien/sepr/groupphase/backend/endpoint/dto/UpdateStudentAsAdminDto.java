package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStudentAsAdminDto extends UpdateStudentDto {

    @NotNull(message = "MatrNumber is mandatory")
    public Long matrNumber;

    public UpdateStudentAsAdminDto() {

    }
}
