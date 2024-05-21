package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplicationUserSubjectsDto extends ApplicationUserDetailDto {
    @NotNull(message = "MatrNumber is mandatory")
    public Long matrNumber;

    private List<UserSubjectDto> subjects;
}
