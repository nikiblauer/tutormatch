package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TopStatisticsDto {
    private List<String> topXofferedSubjects;
    private List<String> topXneededSubjects;
    private List<Integer> topXofferedAmount;
    private List<Integer> topXneededAmount;

    public String toString() {
        return "offered Subjects: " + this.topXofferedSubjects + " amount of times: " + this.topXofferedAmount + " offered Subjects: " + this.topXneededSubjects + " amount of times: " + this.topXneededAmount;
    }
}
