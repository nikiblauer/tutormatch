package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CoverageSubjectsStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TopStatisticsDto;

public interface StatisticService {

    /**
     * Get simple statistics
     * Returns: Registered Users, Ratio of offered to needed subjects, Open chats per user.
     *
     * @return simple statistics
     */
    SimpleStatisticsDto getSimpleStatistics();

    /**
     * Get extended statistics.
     *
     * @param x number of top subjects which are returned to frontend
     * @return top statistics list
     */
    TopStatisticsDto getExtendedStatistics(int x);

    /**
     * Get coverage subjects statistics.
     * Returns those most x requested subjects which are not offered by any tutor in descending order.
     * Returns those most x offered subjects which are not requested by any trainee in descending order.
     * Calculates the differences between offerings and requests, only subjects with a difference >= 5 are returned
     *
     * @param x number of top subjects which are returned to frontend.
     * @return coverage subjects statistics.
     */
    CoverageSubjectsStatisticsDto getCoverageSubjectsStatistics(int x);
}
