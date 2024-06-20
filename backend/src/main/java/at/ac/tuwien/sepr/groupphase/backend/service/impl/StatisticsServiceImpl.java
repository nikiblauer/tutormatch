package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CoverageSubjectsStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TopStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatRoomRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.StatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class StatisticsServiceImpl implements StatisticService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final UserSubjectRepository userSubjectRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public StatisticsServiceImpl(UserRepository userRepository, UserSubjectRepository userSubjectRepository, ChatRoomRepository chatRoomRepository) {
        this.userRepository = userRepository;
        this.userSubjectRepository = userSubjectRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public SimpleStatisticsDto getSimpleStatistics() {
        LOGGER.trace("getSimpleStatistics");
        SimpleStatisticsDto statistics = new SimpleStatisticsDto();
        long registeredUsers = userRepository.countNonAdminUsers();
        long unverifiedUsers = userRepository.countUnverifiedUsers();
        long subjectsOffered = userSubjectRepository.countSubjectsOffered();
        long subjectsNeeded = userSubjectRepository.countSubjectsNeeded();
        double ratioOfferedNeededSubjects = subjectsOffered != 0 ? (double) subjectsNeeded / subjectsOffered : 0.0;

        ratioOfferedNeededSubjects = Math.round(ratioOfferedNeededSubjects * 100.0) / 100.0;
        statistics.setRegisteredVerifiedUsers((int) registeredUsers);
        statistics.setRegisteredUnverifiedUsers((int) unverifiedUsers);
        statistics.setRatioOfferedNeededSubjects(ratioOfferedNeededSubjects);
        statistics.setOpenChatsPerUser((double) chatRoomRepository.count() / 2);
        return statistics;
    }

    @Override
    public TopStatisticsDto getExtendedStatistics(int x) {
        LOGGER.trace("getTopXStatistics");
        TopStatisticsDto statistics = new TopStatisticsDto();
        statistics.setTopXofferedSubjects(userSubjectRepository.getTopXofferedSubjects(x));
        statistics.setTopXneededSubjects(userSubjectRepository.getTopXneededSubjects(x));
        statistics.setTopXofferedAmount(userSubjectRepository.getTopXofferedAmount(x));
        statistics.setTopXneededAmount(userSubjectRepository.getTopXneededAmount(x));
        return statistics;
    }

    @Override
    public CoverageSubjectsStatisticsDto getCoverageSubjectsStatistics(int x) {
        LOGGER.trace("getCoverageSubjectsStatistics");
        CoverageSubjectsStatisticsDto statistics = new CoverageSubjectsStatisticsDto();

        statistics.setMostRequestedSubjectsWithoutCoverage(userSubjectRepository.getMostRequestedSubjectsWithoutCoverage(x));
        statistics.setMostOfferedSubjectsWithoutCoverage(userSubjectRepository.getMostOfferedSubjectsWithoutCoverage(x));

        //requested subjects
        statistics.setNumberOfStudentsOfferedSubjects(userSubjectRepository.getMostOfferedSubjectsWithoutCoverageAmount(x));
        statistics.setNumberOfStudentsRequestedSubjects(userSubjectRepository.getMostRequestedSubjectsWithoutCoverageAmount(x));
        return statistics;
    }
}
