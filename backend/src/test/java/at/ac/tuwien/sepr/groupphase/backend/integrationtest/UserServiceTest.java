package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_BASE_URI;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ActiveProfiles("test")
public class UserServiceTest {
    @Autowired
    private ApplicationUserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void createNewValidUser() throws Exception {
        ApplicationUser user = new ApplicationUser("password", false, "Franz", "U", 133465L, new ContactDetails("+438881919190", "franz@student.tuwien.ac.at"), false);
        CreateApplicationUserDto applicationUserDto = userMapper.mapUserToCreateApplicationUserDto(user, user.getDetails());



        ApplicationUser createdApplicationUser = userService.create(applicationUserDto);

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
