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

    @Query("SELECT COUNT(us) FROM UserSubject us WHERE us.role = 'tutor'")
    long countSubjectsOffered();

    @Query("SELECT COUNT(us) FROM UserSubject us WHERE us.role = 'trainee'")
    long countSubjectsNeeded();

    @Query(value =
        "SELECT CONCAT_WS(' ', s.type, s.title) "
            + "FROM Subject s "
            + "JOIN UserSubject us ON us.subject.id = s.id "
            + "WHERE us.role = 'tutor' "
            + "GROUP BY s.id "
            + "ORDER BY COUNT(us.subject) DESC, s.title ASC "
            + "LIMIT :x")
    List<String> getTopXofferedSubjects(@Param("x") int x);

    @Query(value =
        "SELECT CONCAT_WS(' ', s.type, s.title) "
            + "FROM Subject s "
            + "JOIN UserSubject us ON us.subject.id = s.id "
            + "WHERE us.role = 'trainee' "
            + "GROUP BY s.id "
            + "ORDER BY COUNT(us.subject) DESC, s.title ASC "
            + "LIMIT :x")
    List<String> getTopXneededSubjects(@Param("x") int x);

    @Query(value =
        "SELECT COUNT(us.subject) "
            + "FROM UserSubject us "
            + "WHERE us.role = 'tutor' "
            + "GROUP BY us.subject "
            + "ORDER BY COUNT(us.subject) DESC, us.subject.title ASC "
            + "LIMIT :x")
    List<Integer> getTopXofferedAmount(@Param("x") int x);

    @Query(value =
        "SELECT COUNT(us.subject) "
            + "FROM UserSubject us "
            + "WHERE us.role = 'trainee' "
            + "GROUP BY us.subject "
            + "ORDER BY COUNT(us.subject) DESC, us.subject.title ASC "
            + "LIMIT :x")
    List<Integer> getTopXneededAmount(@Param("x") int x);
}

