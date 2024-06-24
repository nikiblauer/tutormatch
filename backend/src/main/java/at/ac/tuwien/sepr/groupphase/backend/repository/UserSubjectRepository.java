package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CoverageSubjectsStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubjectKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserSubjectRepository extends JpaRepository<UserSubject, UserSubjectKey> {
    List<UserSubject> getUserSubjectByUser(ApplicationUser user);

    @Transactional
    void removeUserSubjectById(UserSubjectKey userId);

    @Transactional
    void deleteUserSubjectsBySubject(Subject subject);

    @Query("SELECT COUNT(us) FROM UserSubject us "
        + "LEFT JOIN Banned b ON us.user.id = b.user.id "
        + "WHERE us.role = 'tutor' AND b.id IS NULL ")
    long countSubjectsOffered();

    @Query("SELECT COUNT(us) FROM UserSubject us "
        + "LEFT JOIN Banned b ON us.user.id = b.user.id "
        + "WHERE us.role = 'trainee' AND b.id IS NULL ")
    long countSubjectsNeeded();

    @Query(value =
        "SELECT CONCAT(s.number, ' ', s.type, ' ', s.title, ' ', '(', s.semester, ')') "
            + "FROM Subject s "
            + "JOIN UserSubject us ON us.subject.id = s.id "
            + "LEFT JOIN Banned b ON us.user.id = b.user.id "
            + "WHERE us.role = 'tutor' AND b.id IS NULL "
            + "GROUP BY s.id "
            + "ORDER BY COUNT(us.subject) DESC, s.title ASC "
            + "LIMIT :x")
    List<String> getTopXofferedSubjects(@Param("x") int x);

    @Query(value =
        "SELECT CONCAT(s.number, ' ', s.type, ' ', s.title, ' ', '(', s.semester, ')') "
            + "FROM Subject s "
            + "JOIN UserSubject us ON us.subject.id = s.id "
            + "LEFT JOIN Banned b ON us.user.id = b.user.id "
            + "WHERE us.role = 'trainee' AND b.id IS NULL "
            + "GROUP BY s.id "
            + "ORDER BY COUNT(us.subject) DESC, s.title ASC "
            + "LIMIT :x")
    List<String> getTopXneededSubjects(@Param("x") int x);

    @Query(value =
        "SELECT COUNT(us.subject) "
            + "FROM UserSubject us "
            + "LEFT JOIN Banned b ON us.user.id = b.user.id "
            + "WHERE us.role = 'tutor' AND b.id IS NULL "
            + "GROUP BY us.subject "
            + "ORDER BY COUNT(us.subject) DESC, us.subject.title ASC "
            + "LIMIT :x")
    List<Integer> getTopXofferedAmount(@Param("x") int x);

    @Query(value =
        "SELECT COUNT(us.subject) "
            + "FROM UserSubject us "
            + "LEFT JOIN Banned b ON us.user.id = b.user.id "
            + "WHERE us.role = 'trainee' AND b.id IS NULL "
            + "GROUP BY us.subject "
            + "ORDER BY COUNT(us.subject) DESC, us.subject.title ASC "
            + "LIMIT :x")
    List<Integer> getTopXneededAmount(@Param("x") int x);

    @Query(value =
        "SELECT new at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CoverageSubjectsStatisticsDto("
            + "CONCAT(s.number, ' ', s.type, ' ', s.title, ' ', '(', s.semester, ')'), "
            + "CAST(SUM(CASE WHEN us.role = 'tutor' THEN 1 ELSE 0 END) AS int), "
            + "CAST(SUM(CASE WHEN us.role = 'trainee' THEN 1 ELSE 0 END) AS int), "
            + "CAST(ABS(SUM(CASE WHEN us.role = 'tutor' THEN 1 ELSE 0 END) - SUM(CASE WHEN us.role = 'trainee' THEN 1 ELSE 0 END)) AS int) "
            + ") "
            + "FROM Subject s "
            + "JOIN UserSubject us ON us.subject.id = s.id "
            + "LEFT JOIN Banned b ON us.subject.id = b.user.id "
            + "WHERE b.id IS NULL "
            + "GROUP BY s.id, s.number, s.type, s.title, s.semester "
            + "HAVING ABS(SUM(CASE WHEN us.role = 'tutor' THEN 1 ELSE 0 END) - SUM(CASE WHEN us.role = 'trainee' THEN 1 ELSE 0 END)) >= 1 "
            + "ORDER BY ABS(SUM(CASE WHEN us.role = 'tutor' THEN 1 ELSE 0 END) - SUM(CASE WHEN us.role = 'trainee' THEN 1 ELSE 0 END)) DESC "
            + "LIMIT :limit")
    List<CoverageSubjectsStatisticsDto> getLowCoverageSubjects(@Param("limit") int limit);
}

