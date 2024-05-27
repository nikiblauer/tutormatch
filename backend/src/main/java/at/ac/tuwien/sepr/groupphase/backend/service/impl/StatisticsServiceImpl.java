package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TopStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
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
    private final SubjectRepository subjectRepository;
    private final UserSubjectRepository userSubjectRepository;

    @Autowired
    public StatisticsServiceImpl(UserRepository userRepository, SubjectRepository subjectRepository, UserSubjectRepository userSubjectRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.userSubjectRepository = userSubjectRepository;
    }

    @Override
    public SimpleStatisticsDto getSimpleStatistics() {
        SimpleStatisticsDto statistics = new SimpleStatisticsDto();
        long registeredUsers = userRepository.countNonAdminUsers();
        long subjectsOffered = userSubjectRepository.countSubjectsOffered();
        long subjectsNeeded = userSubjectRepository.countSubjectsNeeded();
        double ratioOfferedNeededSubjects = subjectsOffered != 0 ? (double) subjectsNeeded / subjectsOffered : 0.0;
        ratioOfferedNeededSubjects = Math.round(ratioOfferedNeededSubjects * 100.0) / 100.0;
        statistics.setRegisteredVerifiedUsers((int) registeredUsers);
        statistics.setRatioOfferedNeededSubjects(ratioOfferedNeededSubjects);

        //TODO open chats, wait till chat is implemented, return now 5 for example value
        statistics.setOpenChatsPerUser(5);
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
}
