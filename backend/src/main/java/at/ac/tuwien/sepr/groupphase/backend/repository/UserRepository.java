package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findApplicationUserByDetails_Email(String email);

    List<ApplicationUser> findAllByDetails_Email(String email);

    ApplicationUser findApplicationUsersById(Long id);

}



