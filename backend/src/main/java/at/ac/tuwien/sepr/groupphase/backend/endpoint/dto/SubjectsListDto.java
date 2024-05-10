package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class SubjectsListDto {
    public List<Long> traineeSubjects;
    public List<Long> tutorSubjects;
}
