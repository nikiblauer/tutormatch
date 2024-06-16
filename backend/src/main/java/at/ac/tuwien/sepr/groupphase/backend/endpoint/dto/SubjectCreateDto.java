package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubjectCreateDto {
    private String title;
    private String type;
    private String number;
    private String semester;
    private String url;
    @Size(max = 1000)
    private String description;
}
