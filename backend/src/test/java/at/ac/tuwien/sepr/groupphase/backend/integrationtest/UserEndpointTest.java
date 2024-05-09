package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectsListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import java.util.ArrayList;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.DEFAULT_USER_EMAIL;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_BASE_URI;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_ROLES;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private UserRepository userRepository;

    @Autowired
    private ApplicationUserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Test
    public void createNewValidUser() throws Exception {
        ApplicationUser user = new ApplicationUser("password", false, "Konsti", "U", 123465L, new ContactDetails("+438881919190", "konsti@tuwien.ac.at"));
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

        ApplicationUserDto createdApplicationUserDto = objectMapper.readValue(response.getContentAsString(),
            ApplicationUserDto.class);

        assertAll(
            () -> assertNotNull(createdApplicationUserDto),
            () -> assertEquals(createdApplicationUserDto.getEmail(), applicationUserDto.getEmail()),
            () -> assertEquals(createdApplicationUserDto.getFirstname(), applicationUserDto.getFirstname()),
            () -> assertEquals(createdApplicationUserDto.getLastname(), applicationUserDto.getLastname()),
            () -> assertEquals(createdApplicationUserDto.getMatrNumber(), applicationUserDto.getMatrNumber()),
            () -> assertEquals(createdApplicationUserDto.getTelNr(), applicationUserDto.getTelNr()),
            () -> assertTrue(passwordEncoder.matches(applicationUserDto.getPassword(), createdApplicationUserDto.getPassword()))
        );

    }

    @Test
    public void createNewInvalidUser_422() throws Exception {
        ApplicationUser user = new ApplicationUser("", false, "", "", 123465L, new ContactDetails("+438881919190", "konsti@tuswien.ac.at"));
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
    public void putValidUserSubject_Returns200() throws Exception {

        SubjectsListDto subjectsListDto = new SubjectsListDto();
        subjectsListDto.traineeSubjects = new ArrayList<>();
        subjectsListDto.traineeSubjects.add(1L);
        subjectsListDto.traineeSubjects.add(2L);
        subjectsListDto.tutorSubjects = new ArrayList<>();
        subjectsListDto.tutorSubjects.add(3L);
        subjectsListDto.tutorSubjects.add(4L);

        String body = objectMapper.writeValueAsString(subjectsListDto);

        MvcResult mvcResult = this.mockMvc.perform(put(USER_BASE_URI+"/1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNull(response.getContentType());
    }
    @Test
    public void putInvalidUserForUserSubject_Returns404NotFound() throws Exception {

        SubjectsListDto subjectsListDto = new SubjectsListDto();
        subjectsListDto.traineeSubjects = new ArrayList<>();
        subjectsListDto.traineeSubjects.add(1L);
        subjectsListDto.traineeSubjects.add(2L);
        subjectsListDto.tutorSubjects = new ArrayList<>();
        subjectsListDto.tutorSubjects.add(3L);
        subjectsListDto.tutorSubjects.add(4L);

        String body = objectMapper.writeValueAsString(subjectsListDto);

        MvcResult mvcResult = this.mockMvc.perform(put(USER_BASE_URI+"/-1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_EMAIL, USER_ROLES)))
            .andExpect(status().isNotFound())
            .andDo(print())
            .andReturn()

            ;
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                assertEquals(38, content.length());
            }
        );
    }

    @Test
    public void putInvalidSubjectsForUSerSubject_Returns422() throws Exception {

        SubjectsListDto subjectsListDto = new SubjectsListDto();
        subjectsListDto.traineeSubjects = new ArrayList<>();
        subjectsListDto.traineeSubjects.add(1L);
        subjectsListDto.traineeSubjects.add(2L);
        subjectsListDto.tutorSubjects = new ArrayList<>();
        subjectsListDto.tutorSubjects.add(2L);
        subjectsListDto.tutorSubjects.add(3L);

        String body = objectMapper.writeValueAsString(subjectsListDto);

        MvcResult mvcResult = this.mockMvc.perform(put(USER_BASE_URI+"/1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_EMAIL, USER_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                assertEquals(123, content.length());
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
