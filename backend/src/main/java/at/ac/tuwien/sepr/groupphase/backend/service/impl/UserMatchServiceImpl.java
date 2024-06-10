package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserMatchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserRating;
import at.ac.tuwien.sepr.groupphase.backend.repository.RatingRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.RatingService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserMatchService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

@Service
public class UserMatchServiceImpl implements UserMatchService {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingService ratingService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Override
    public Stream<UserMatchDto> findMatchingsForUser(String email) {
        LOGGER.trace("findMatchingUserByUserIdAsStream({})", email);

        ApplicationUser user = userRepository.findApplicationUserByDetails_Email(email);


        String queryString = "SELECT u.Id, "
            + "u.FIRSTNAME, "
            + "u.LASTNAME, "
            + "SUM(CASE WHEN us1.ROLE = 'trainee' THEN 1 ELSE 0 END) AS trainee_matchingCount, "
            + "SUM(CASE WHEN us1.ROLE = 'tutor' THEN 1 ELSE 0 END) AS tutor_matchingCount, "
            + "COUNT(*) AS total_matchingCount, "
            + "GROUP_CONCAT(CASE WHEN us1.ROLE = 'trainee' THEN CONCAT(s.NUMBER, ' ', s.TITLE) END SEPARATOR ', ') AS trainee_subjects,"
            + "GROUP_CONCAT(CASE WHEN us1.ROLE = 'tutor' THEN CONCAT(s.NUMBER, ' ', s.TITLE) END SEPARATOR ', ') AS tutor_subjects,"
            + "FROM APPLICATION_USER u "
            + "JOIN USER_SUBJECT us1 ON u.id = us1.USER_ID "
            + "JOIN USER_SUBJECT us2 ON us1.SUBJECT_ID = us2.SUBJECT_ID "
            + "AND us1.ROLE != us2.ROLE "
            + "AND us1.USER_ID != us2.USER_ID "
            + "JOIN SUBJECT s ON us1.SUBJECT_ID = s.ID "
            + "WHERE u.ID != :userId AND us2.USER_ID = :userId "
            + "AND NOT EXISTS (SELECT 1 FROM Banned b WHERE u.ID = b.USER_ID) " // Exclude users in Ban table
            + "GROUP BY u.id "
            + "HAVING trainee_matchingCount > 0 AND tutor_matchingCount > 0 "
            + "ORDER BY total_matchingCount DESC";

        Query query = entityManager.createNativeQuery(queryString);

        query.setParameter("userId", user.getId());
        query.setMaxResults(100);


        return query.getResultList()
            .stream()
            .map(objItem -> {
                var item = (Object[]) objItem;
                var rating = ratingService.getRatingOfStudent((Long) item[0]);
                return UserMatchDto
                    .builder()
                    .id((Long) item[0])
                    .firstname((String) item[1])
                    .lastname((String) item[2])
                    .traineeMatchingcount((Long) item[3])
                    .tutorMatchingcount((Long) item[4])
                    .totalMatchingcount((Long) item[5])
                    .traineeSubjects((String) item[6])
                    .tutorSubjects((String) item[7])
                    .rating(rating[0])
                    .amount((long) rating[1])
                    .build();
            });
    }
}
