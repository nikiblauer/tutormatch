package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object for the coverage statistics of subjects.
 */
@Getter
@Setter
public class CoverageSubjectsStatisticsDto {

    private String subjectInfo; // subject title, subject type and number
    private int numOfTutors;
    private int numOfTrainees;
    private int diff; // calculated difference between tutors and trainees

    public CoverageSubjectsStatisticsDto(String subjectInfo, int numOfTutors, int numOfTrainees, int diff) {
        this.subjectInfo = subjectInfo;
        this.numOfTutors = numOfTutors;
        this.numOfTrainees = numOfTrainees;
        this.diff = diff;
    }

    public CoverageSubjectsStatisticsDto() {
    }

    @Override
    public String toString() {
        return "CoverageSubjectsStatisticsDto{"
            + "subjectInfo='" + subjectInfo + '\''
            + ", numOfTutors=" + numOfTutors
            + ", numOfTrainees=" + numOfTrainees
            + ", diff=" + diff
            + '}';
    }
}
