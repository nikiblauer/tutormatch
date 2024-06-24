package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findApplicationUserByDetails_Email(String email);

    @Query("SELECT u FROM ApplicationUser u WHERE u.admin = false "
        + "AND (:hasBan IS NULL OR (:hasBan = true AND u.ban IS NOT NULL) OR (:hasBan = false AND u.ban IS NULL)) "
        + "AND (:verified IS NULL OR u.verified = :verified) "
        + "AND ((:fullname IS NULL AND :matrNumber IS NULL) "
        + "OR (LOWER(CONCAT(u.firstname, ' ', u.lastname)) LIKE LOWER(CONCAT('%', :fullname, '%')) OR (LOWER(CONCAT(u.lastname, ' ', u.firstname)) LIKE LOWER(CONCAT('%', :fullname, '%')))) "
        + "OR CAST(u.matrNumber AS string) LIKE CONCAT('%', :matrNumber, '%'))")
    Page<ApplicationUser> findAllByFullnameOrMatrNumber(@Param("fullname") String fullname, @Param("matrNumber") Long matrNumber, @Param("hasBan") Boolean hasBan, @Param("verified") Boolean verified, Pageable pageable);

    @Query("SELECT s.title FROM ApplicationUser u JOIN u.userSubjects us JOIN us.subject s WHERE u.id = :userId AND us.role = :role")
    List<String> getUserSubjectsByRole(@Param("userId") Long id, @Param("role") String role);

    @Query("SELECT COUNT(u) FROM ApplicationUser u WHERE u.admin = false AND u.verified = true")
    long countNonAdminUsers();

    @Query("SELECT COUNT(u) FROM ApplicationUser u WHERE u.admin = false AND u.verified = false")
    long countUnverifiedUsers();
}



