package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * This DTO represents the statistics, of which subjects have a lot of requests (trainee)
 * but no coverage which means no one is offering this subject.
 * only subjects with a difference >= 5 are returned (requested - offered)
 * Returns an x amount of results
 */
@Getter
@Setter
public class CoverageSubjectsStatisticsDto {
    List<String> mostRequestedSubjectsWithoutCoverage;
    List<String> mostOfferedSubjectsWithoutCoverage;

    List<String> numberOfStudentsRequestedSubjects;
    List<String> numberOfStudentsOfferedSubjects;
}
