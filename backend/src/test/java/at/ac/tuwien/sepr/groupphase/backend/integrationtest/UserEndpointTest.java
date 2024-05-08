package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_BASE_URI;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
@AutoConfigureMockMvc
public class UserEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createNewValidUser() throws Exception {
        ApplicationUser user = new ApplicationUser("password", false, "Konsti", "U", 123465L, new ContactDetails("+436767720870", "konsti@tuwien.ac.at"));
        ApplicationUserDto applicationUserDto = userMapper.mapUserToDto(user, user.getDetails());
        String body = objectMapper.writeValueAsString(applicationUserDto);

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        ApplicationUserDto applicationUserDto1 = objectMapper.readValue(response.getContentAsString(),
            ApplicationUserDto.class);

        assertEquals(applicationUserDto.toString(), applicationUserDto1.toString());
    }

    @Test
    void createNewInvalidUser_422() throws Exception {
        ApplicationUser user = new ApplicationUser("", false, "", "", 123465L, new ContactDetails("+436767720870", "konsti@tuswien.ac.at"));
        ApplicationUserDto applicationUserDto = userMapper.mapUserToDto(user, user.getDetails());
        String body = objectMapper.writeValueAsString(applicationUserDto);

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                assertEquals(145, content.length());
            }
        );
    }

    @Test
    void testUpdateUser() throws Exception {
        // Create an ApplicationUserDto object with the updated user details
        ApplicationUserDto updatedUser = new ApplicationUserDto();
        updatedUser.setFirstname("UserUpdated");
        updatedUser.setLastname("SurnameUpdated");
        updatedUser.setPassword("NewPassword123");
        updatedUser.setMatrNumber(1211646L);
        updatedUser.setEmail("updateduser@tuwien.ac.at");
        updatedUser.setTelNr("+4367675553");

        // Retrieve all users and get the ID of the first user
        List<ApplicationUser> users = userRepository.findAll();
        Long userIdToUpdate = users.get(0).getId();

        // Perform a PUT request to the "/api/v1/user/{id}" endpoint
        MvcResult result = mockMvc.perform(put("/api/v1/user/" + userIdToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isOk())
            .andReturn();

        // Parse the response
        ApplicationUserDto returnedUser = objectMapper.readValue(result.getResponse().getContentAsString(), ApplicationUserDto.class);

        // Assert that the returned user has the updated details
        assertAll(
            () -> assertEquals(updatedUser.getFirstname(), returnedUser.getFirstname()),
            () -> assertEquals(updatedUser.getLastname(), returnedUser.getLastname()),
            () -> assertEquals(updatedUser.getMatrNumber(), returnedUser.getMatrNumber()),
            () -> assertEquals(updatedUser.getEmail(), returnedUser.getEmail()),
            () -> assertEquals(updatedUser.getTelNr(), returnedUser.getTelNr())
        );
    }
}
