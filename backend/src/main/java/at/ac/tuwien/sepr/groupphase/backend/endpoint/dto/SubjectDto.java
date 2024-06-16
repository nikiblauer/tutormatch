package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectDto {
    private String name;
    private String url;
    private String description;
    private Long id;
}
