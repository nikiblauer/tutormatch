package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestUtils;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailsWithSubjectDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.helper.RestResponsePage;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_EMAIL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private SubjectRepository subjectRepository;

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
        RestResponsePage<ApplicationUserDto> returnedPage = objectMapper.readValue(responseBody, new TypeReference<RestResponsePage<ApplicationUserDto>>() {
        });
        List<ApplicationUserDto> returnedUsers = returnedPage.getContent();

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
        RestResponsePage<ApplicationUserDto> returnedPage = objectMapper.readValue(responseBody, new TypeReference<RestResponsePage<ApplicationUserDto>>() {
        });
        List<ApplicationUserDto> returnedUsers = returnedPage.getContent();

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
        UserDetailsWithSubjectDto returnedUser = objectMapper.readValue(responseBody, UserDetailsWithSubjectDto.class);

        assertAll("User",
            () -> assertEquals("User1", returnedUser.getFirstname()),
            () -> assertEquals("Surname1", returnedUser.getLastname()),
            () -> assertEquals("e10000001@student.tuwien.ac.at", returnedUser.getEmail()),
            () -> assertEquals("+43660 1111111", returnedUser.getTelNr()),
            () -> assertEquals(1000, returnedUser.getAreaCode()),
            () -> assertEquals("Wien", returnedUser.getCity()),
            () -> assertEquals("[Advanced Information Retrieval, Advanced Model Engineering, Advanced Model Engineering, Advanced Modeling and Simulation]", Arrays.toString(returnedUser.getTutorSubjects())),
            () -> assertEquals("[3D Vision, 3D Vision, AKNUM Reinforcement Learning, Abstrakte Maschinen]", Arrays.toString(returnedUser.getTraineeSubjects()))
        );
    }

    private String loginAsAdmin() throws Exception {
        String loginData = "{\"password\": \"Password123\", \"email\": \"" + ADMIN_EMAIL + "\"}";
        ArrayList<String> expectedRole = new ArrayList<>();
        expectedRole.add("ROLE_ADMIN");
        return TestUtils.validLoginTest(mockMvc, loginData, expectedRole, ADMIN_EMAIL, securityProperties);
    }
    @Test
    void CreateNewValidSubjectReturnsSubject_200() throws Exception {
        // get the first user from the database
        SubjectCreateDto subjectDetailDto = new SubjectCreateDto();
        subjectDetailDto.setUrl("http://testURL");
        subjectDetailDto.setSemester("2020WS");
        subjectDetailDto.setDescription("Test Description");
        subjectDetailDto.setType("UE");
        subjectDetailDto.setNumber("199.199");
        subjectDetailDto.setTitle("Title");

        String token = loginAsAdmin();

        String body = objectMapper.writeValueAsString(subjectDetailDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/admin/subject")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        SubjectDetailDto returnedUser = objectMapper.readValue(responseBody, SubjectDetailDto.class);

        assertAll(
            () -> assertEquals("http://testURL", returnedUser.getUrl()),
            () -> assertEquals("2020WS", returnedUser.getSemester()),
            () -> assertEquals("Test Description", returnedUser.getDescription()),
            () -> assertEquals("UE", returnedUser.getType()),
            () -> assertEquals("199.199", returnedUser.getNumber()),
            () -> assertEquals("Title", returnedUser.getTitle())
        );
    }
    @Test
    void UpdateNewValidSubjectReturnsSubject_200() throws Exception {
        // get the first user from the database
        SubjectDetailDto subjectDetailDto = new SubjectDetailDto();
        subjectDetailDto.setUrl("http://testURL");
        subjectDetailDto.setSemester("2020WS");
        subjectDetailDto.setDescription("Test Description");
        subjectDetailDto.setType("UE");
        subjectDetailDto.setNumber("199.199");
        subjectDetailDto.setTitle("Title");
        subjectDetailDto.setId(subjectRepository.findAll().get(0).getId());

        String token = loginAsAdmin();

        String body = objectMapper.writeValueAsString(subjectDetailDto);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/admin/subject")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        SubjectDetailDto returnedUser = objectMapper.readValue(responseBody, SubjectDetailDto.class);

        assertAll(
            () -> assertEquals("http://testURL", returnedUser.getUrl()),
            () -> assertEquals("2020WS", returnedUser.getSemester()),
            () -> assertEquals("Test Description", returnedUser.getDescription()),
            () -> assertEquals("UE", returnedUser.getType()),
            () -> assertEquals("199.199", returnedUser.getNumber()),
            () -> assertEquals("Title", returnedUser.getTitle()),
            () -> assertEquals(subjectRepository.findAll().get(0).getId(), returnedUser.getId())

        );
    }
    @Test
    void DeleteValidSubjectReturnsSubject_200() throws Exception {

        Long id = subjectRepository.findAll().get(0).getId();

        String token = loginAsAdmin();

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/admin/" + id)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    }
}
