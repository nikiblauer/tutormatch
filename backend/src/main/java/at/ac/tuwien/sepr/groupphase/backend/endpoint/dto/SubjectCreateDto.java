package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

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
    private String description;
}
