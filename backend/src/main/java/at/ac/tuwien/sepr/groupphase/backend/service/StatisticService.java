package at.ac.tuwien.sepr.groupphase.backend.service;

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
}
