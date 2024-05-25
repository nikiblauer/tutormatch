package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswordResetDto;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ActiveProfiles({"test", "generateData"})
@Transactional
public class LoginServiceTest {

    @Autowired
    private ApplicationUserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Test
    public void userCannotUseOldPasswordAfterPasswordChange() throws Exception {
        String oldPassword = "password";
        ApplicationUser user = new ApplicationUser(oldPassword, false, "Franz", "U", 133465L, new ContactDetails("+438881919190", "franz@student.tuwien.ac.at", new Address( "Teststraße 2", 1200, "Wien")), true);
        userRepository.save(user);
        PasswordResetDto passwordChangeDto = new PasswordResetDto();
        String newPassword = "password1";
        passwordChangeDto.setPassword(newPassword);
        passwordChangeDto.setRepeatPassword(newPassword);
        String token = jwtTokenizer.buildVerificationToken(user.getDetails().getEmail());
        boolean changed = loginService.changePasswordWithToken(token, passwordChangeDto);
        ApplicationUser updatedUser = userService.findApplicationUserByEmail(user.getDetails().getEmail());
        assertTrue(changed);
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
        assertFalse(passwordEncoder.matches(oldPassword, updatedUser.getPassword()));
    }

}
