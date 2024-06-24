package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Subject findSubjectById(Long id);

    List<Subject> findAll();

    /**
     * query all subjects by a string in the combination of field type, number and title.
     *
     * @param searchParam the query string
     * @param pageable    limit of results
     * @return a list of Subjects
     */
    @Query("SELECT s FROM Subject s where (:searchParam is NULL OR LOWER(concat(s.type, ' ', s.number, ' ', s.title)) LIKE LOWER(concat('%', :searchParam, '%')))")
    Page<Subject> findAllSubjectByQueryParam(@Param("searchParam") String searchParam, Pageable pageable);


    boolean existsByTypeAndSemesterAndNumber(String type, String semester, String number);

}
