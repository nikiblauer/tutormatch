package at.ac.tuwien.sepr.groupphase.backend.repository;

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
        "SELECT CONCAT(s.number, ' ', s.type, ' ', s.title, ' ', '(', s.semester, ')') "
            + "FROM Subject s "
            + "JOIN UserSubject us ON us.subject.id = s.id "
            + "LEFT JOIN Banned b ON us.user.id = b.user.id "
            + "WHERE us.role = 'trainee' AND b.id IS NULL "
            + "GROUP BY s.id "
            + "HAVING ABS(COUNT(us.subject) - (SELECT COUNT(us2.subject) FROM UserSubject us2 "
            + "WHERE us2.role = 'tutor' AND us2.subject.id = s.id)) >= 1 "
            + "ORDER BY ABS(COUNT(us.subject) - (SELECT COUNT(us2.subject) FROM UserSubject us2 "
            + "WHERE us2.role = 'tutor' AND us2.subject.id = s.id)) DESC "
            + "LIMIT :x")
    List<String> getMostRequestedSubjectsWithoutCoverage(@Param("x") int x);

    @Query(value =
        "SELECT CONCAT(s.number, ' ', s.type, ' ', s.title, ' ', '(', s.semester, ')') "
            + "FROM Subject s "
            + "JOIN UserSubject us ON us.subject.id = s.id "
            + "LEFT JOIN Banned b ON us.user.id = b.user.id "
            + "WHERE us.role = 'tutor' and b.id IS NULL "
            + "GROUP BY s.id "
            + "HAVING ABS(COUNT(us.subject) - (SELECT COUNT(us2.subject) FROM UserSubject us2 "
            + "WHERE us2.role = 'trainee' AND us2.subject.id = s.id)) >= 1 "
            + "ORDER BY ABS(COUNT(us.subject) - (SELECT COUNT(us2.subject) FROM UserSubject us2 "
            + "WHERE us2.role = 'trainee' AND us2.subject.id = s.id)) DESC "
            + "LIMIT :x")
    List<String> getMostOfferedSubjectsWithoutCoverage(@Param("x") int x);

    @Query(value =
        "SELECT CONCAT('Trainees: ', COUNT(us.subject), ', Tutors: ', (SELECT COUNT(us2.subject) FROM UserSubject us2 WHERE us2.role = 'tutor' AND us2.subject.id = s.id)) "
            + "FROM Subject s "
            + "JOIN UserSubject us ON us.subject.id = s.id "
            + "LEFT JOIN Banned b ON us.user.id = b.user.id "
            + "WHERE us.role = 'trainee' AND b.id IS NULL "
            + "GROUP BY s.id "
            + "HAVING ABS(COUNT(us.subject) - (SELECT COUNT(us2.subject) FROM UserSubject us2 "
            + "WHERE us2.role = 'tutor' AND us2.subject.id = s.id)) >= 1 "
            + "ORDER BY ABS(COUNT(us.subject) - (SELECT COUNT(us2.subject) FROM UserSubject us2 "
            + "WHERE us2.role = 'tutor' AND us2.subject.id = s.id)) DESC "
            + "LIMIT :x")
    List<String> getMostRequestedSubjectsWithoutCoverageAmount(@Param("x") int x);

    @Query(value =
        "SELECT CONCAT('Tutors: ', COUNT(us.subject), ', Trainees: ', (SELECT COUNT(us2.subject) FROM UserSubject us2 WHERE us2.role = 'trainee' AND us2.subject.id = s.id)) "
            + "FROM Subject s "
            + "JOIN UserSubject us ON us.subject.id = s.id "
            + "LEFT JOIN Banned b ON us.user.id = b.user.id "
            + "WHERE us.role = 'tutor' AND b.id IS NULL "
            + "GROUP BY s.id "
            + "HAVING ABS(COUNT(us.subject) - (SELECT COUNT(us2.subject) FROM UserSubject us2 "
            + "WHERE us2.role = 'trainee' AND us2.subject.id = s.id)) >= 1 "
            + "ORDER BY ABS(COUNT(us.subject) - (SELECT COUNT(us2.subject) FROM UserSubject us2 "
            + "WHERE us2.role = 'trainee' AND us2.subject.id = s.id)) DESC "
            + "LIMIT :x")
    List<String> getMostOfferedSubjectsWithoutCoverageAmount(@Param("x") int x);
}

