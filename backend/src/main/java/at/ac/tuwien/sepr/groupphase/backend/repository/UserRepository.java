package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

//TODO: replace this class with a correct ApplicationUser JPARepository implementation
@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    /*
    private final ApplicationUser user;
    private final ApplicationUser admin;

    @Autowired
    public UserRepository(PasswordEncoder passwordEncoder) {
        user = new ApplicationUser("user@email.com", passwordEncoder.encode("password"), false);
        admin = new ApplicationUser("admin@email.com", passwordEncoder.encode("password"), true);
    }

    public ApplicationUser findUserByEmail(String email) {
        if (email.equals(user.getEmail())) {
            return user;
        }
        if (email.equals(admin.getEmail())) {
            return admin;
        }
        return null; // In this case null is returned to fake Repository behavior
    }*/
    ApplicationUser findApplicationUserByDetails_Email(String email);

    void deleteApplicationUserByDetailsNull();
}



