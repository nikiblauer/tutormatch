package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestUtils;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_EMAIL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
@AutoConfigureMockMvc
public class AdminEndpointTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Test
    void testQueryUser2Surname2() throws Exception {
        String token = loginAsAdmin();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/users")
                .param("fullname", "User2 Surname2")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ApplicationUserDto> returnedUsers = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertEquals(1, returnedUsers.size());
        assertEquals("User2", returnedUsers.get(0).getFirstname());
        assertEquals("Surname2", returnedUsers.get(0).getLastname());
    }

    @Test
    void testQueryUnknownUser() throws Exception {
        String token = loginAsAdmin();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/users")
                .param("fullname", "sdfgsdfgsdfgdgfdfg lasdflalasdflasdflalasdf")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ApplicationUserDto> returnedUsers = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertEquals(0, returnedUsers.size()); //empty list
    }

    @Test
    void testGetUserDetailsFromUserWithId1() throws Exception {
        // get the first user from the database
        List<ApplicationUser> users = userRepository.findAll();
        Long userId = users.get(0).getId();

        String token = loginAsAdmin();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/users/" + userId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        ApplicationUserDto returnedUser = objectMapper.readValue(responseBody, ApplicationUserDto.class);

        assertAll("User",
            () -> assertEquals("User1", returnedUser.getFirstname()),
            () -> assertEquals("Surname1", returnedUser.getLastname()),
            () -> assertEquals(10000001, returnedUser.getMatrNumber()),
            () -> assertEquals(userId, returnedUser.getId())
        );
    }

    private String loginAsAdmin() throws Exception {
        String loginData = "{\"password\": \"Password123\", \"email\": \"" + ADMIN_EMAIL + "\"}";
        ArrayList<String> expectedRole = new ArrayList<>();
        expectedRole.add("ROLE_ADMIN");
        return TestUtils.validLoginTest(mockMvc, loginData, expectedRole, ADMIN_EMAIL, securityProperties);
    }
}
