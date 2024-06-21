package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswordResetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ActiveProfiles({"test", "generateData"})
@Transactional
public class UserServiceTest {
    @Autowired
    private ApplicationUserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Test
    public void createNewValidUser() throws Exception {
        ApplicationUser user = new ApplicationUser("password", false, "Franz", "U", 133465L, new ContactDetails("+438881919190", "franz@student.tuwien.ac.at", new Address( "Teststraße 2", 1200, "Wien")), false);
        CreateStudentDto applicationUserDto = userMapper.mapUserToCreateApplicationUserDto(user);


        ApplicationUser createdApplicationUser = userService.create(applicationUserDto, "test.at");

        assertTrue(passwordEncoder.matches(user.getPassword(), createdApplicationUser.getPassword()));

        assertAll(
            () -> assertNotNull(createdApplicationUser),
            () -> assertEquals(createdApplicationUser.getDetails().getEmail(), applicationUserDto.getEmail()),
            () -> assertEquals(createdApplicationUser.getFirstname(), applicationUserDto.getFirstname()),
            () -> assertEquals(createdApplicationUser.getLastname(), applicationUserDto.getLastname()),
            () -> assertEquals(createdApplicationUser.getMatrNumber(), applicationUserDto.getMatrNumber()),
            () -> assertTrue(passwordEncoder.matches(applicationUserDto.getPassword(), createdApplicationUser.getPassword()))
        );

    }

}
