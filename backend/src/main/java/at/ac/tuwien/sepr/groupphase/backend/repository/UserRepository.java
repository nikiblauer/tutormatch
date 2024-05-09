package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findApplicationUserByDetails_Email(String email);

    List<ApplicationUser> findAllByDetails_Email(String email);

    @Query("SELECT u FROM ApplicationUser u WHERE (:fullname IS NULL AND :matrNumber IS NULL) "
        + "OR (u.firstname LIKE CONCAT('%', :fullname, '%') OR :fullname LIKE CONCAT('%', u.firstname, '%')) "
        + "OR (u.lastname LIKE CONCAT('%', :fullname, '%') OR :fullname LIKE CONCAT('%', u.lastname, '%')) "
        + "OR CAST(u.matrNumber AS string) LIKE CONCAT('%', :matrNumber, '%')")
    List<ApplicationUser> findAllByFullnameOrMatrNumber(@Param("fullname") String fullname, @Param("matrNumber") Long matrNumber);

    ApplicationUser findApplicationUsersById(Long id);
}



