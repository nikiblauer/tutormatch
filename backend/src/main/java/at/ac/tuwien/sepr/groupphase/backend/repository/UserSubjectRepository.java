package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubjectRepository extends JpaRepository<UserSubject, Long> {
}
