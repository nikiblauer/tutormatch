package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    @Query("SELECT ub.blockedUser.id FROM UserBlock ub WHERE ub.user.id = :userId")
    List<Long> getBlockedUsers(@Param("userId") Long userId);

    @Query("SELECT ub FROM UserBlock ub WHERE ub.user.id = :userId AND ub.blockedUser.id = :userIdToUnblock")
    Optional<UserBlock> findByUserAndBlockedUser(@Param("userId") Long userId, @Param("userIdToUnblock") Long userIdToUnblock);
}
