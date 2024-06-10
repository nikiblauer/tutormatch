package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestUtils;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BanReasonDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentSubjectInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentAsAdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TopStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserBanDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.helper.RestResponsePage;
import at.ac.tuwien.sepr.groupphase.backend.repository.BanRepository;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_EMAIL;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.DEFAULT_USER_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
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

    @Autowired
    private BanRepository banRepository;

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
        RestResponsePage<StudentDto> returnedPage = objectMapper.readValue(responseBody, new TypeReference<RestResponsePage<StudentDto>>() {
        });
        List<StudentDto> returnedUsers = returnedPage.getContent();

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
        RestResponsePage<StudentDto> returnedPage = objectMapper.readValue(responseBody, new TypeReference<RestResponsePage<StudentDto>>() {
        });
        List<StudentDto> returnedUsers = returnedPage.getContent();

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
        StudentSubjectInfoDto returnedUser = objectMapper.readValue(responseBody, StudentSubjectInfoDto.class);

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

    private String loginAsUser() throws Exception {
        String loginData = "{\"password\": \"Password123\", \"email\": \"" + DEFAULT_USER_EMAIL + "\"}";
        ArrayList<String> expectedRole = new ArrayList<>();
        expectedRole.add("ROLE_USER");
        return TestUtils.validLoginTest(mockMvc, loginData, expectedRole, DEFAULT_USER_EMAIL, securityProperties);
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

    @Test
    void getValidUserReturnsUser_200() throws Exception {

        ApplicationUser user = userRepository.findAll().get(0);
        Long id = user.getId();

        String token = loginAsAdmin();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/users/" + id)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        StudentSubjectInfoDto returnedUser = objectMapper.readValue(responseBody, StudentSubjectInfoDto.class);
        assertAll(
            () -> assertEquals(user.getFirstname(), returnedUser.getFirstname()),
            () -> assertEquals(user.getLastname(), returnedUser.getLastname()),
            () -> assertEquals(user.getDetails().getTelNr(), returnedUser.getTelNr()),
            () -> assertEquals(user.getDetails().getEmail(), returnedUser.getEmail())
        );

    }

    @Test
    void changeValidUserDetailsReturnsChangedUser_200() throws Exception {

        ApplicationUser userBefore = userRepository.findAll().get(0);

        UpdateStudentAsAdminDto updatedUser = new UpdateStudentAsAdminDto();
        updatedUser.setId(userBefore.getId());
        updatedUser.setFirstname("UserUpdated");
        updatedUser.setLastname("SurnameUpdated");
        updatedUser.setTelNr("+4367675553");
        updatedUser.setMatrNumber(1111111111L);
        updatedUser.setStreet("newStreet 54");
        updatedUser.setAreaCode(1310);
        updatedUser.setCity("Graz");

        String token = loginAsAdmin();

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/admin/users/update")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        UpdateStudentAsAdminDto returnedUser = objectMapper.readValue(responseBody, UpdateStudentAsAdminDto.class);
        assertAll(
            () -> assertEquals(updatedUser.getFirstname(), returnedUser.getFirstname()),
            () -> assertEquals(updatedUser.getLastname(), returnedUser.getLastname()),
            () -> assertEquals(updatedUser.getTelNr(), returnedUser.getTelNr()),
            () -> assertEquals(updatedUser.getStreet(), returnedUser.getStreet()),
            () -> assertEquals(updatedUser.getAreaCode(), returnedUser.getAreaCode()),
            () -> assertEquals(updatedUser.getCity(), returnedUser.getCity()),
            () -> assertEquals(updatedUser.getMatrNumber(), returnedUser.getMatrNumber()),
            () -> assertNotEquals(userBefore.getFirstname(), returnedUser.getFirstname()),
            () -> assertNotEquals(userBefore.getLastname(), returnedUser.getLastname()),
            () -> assertNotEquals(userBefore.getDetails().getTelNr(), returnedUser.getTelNr()),
            () -> assertNotEquals(userBefore.getDetails().getAddress().getStreet(), returnedUser.getStreet()),
            () -> assertNotEquals(userBefore.getDetails().getAddress().getAreaCode(), returnedUser.getAreaCode()),
            () -> assertNotEquals(userBefore.getDetails().getAddress().getCity(), returnedUser.getCity())
        );

    }

    @Test
    void getUserWithNonAdminTokenReturns_403() throws Exception {

        Long id = subjectRepository.findAll().get(0).getId();

        String token = loginAsUser();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/users/" + id)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andReturn();
    }

    @Test
    void testGetSimpleStatistics() throws Exception {
        String token = loginAsAdmin();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/statistics/simple")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        SimpleStatisticsDto returnedStatistics = objectMapper.readValue(responseBody, SimpleStatisticsDto.class);

        assertAll("Statistics",
            () -> assertEquals(10, returnedStatistics.getRegisteredVerifiedUsers()),
            () -> assertEquals(1.0, returnedStatistics.getRatioOfferedNeededSubjects(), 0.01) // delta is used to compare doubles
        );
    }

    @Test
    void testGetTop5Statistics() throws Exception {
        String token = loginAsAdmin();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/statistics/extended?x=5")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        TopStatisticsDto returnedStatistics = objectMapper.readValue(responseBody, TopStatisticsDto.class);

        List<String> top5OfferedSubjects = Arrays.asList(
            "SE Advanced Model Engineering", "VU Advanced Modeling and Simulation", "PR Advanced Software Engineering",
            "VU Advanced Information Retrieval", "VU Advanced Model Engineering"
        );
        List<String> top5NeededSubjects = Arrays.asList(
            "VU Advanced Modeling and Simulation", "PR Advanced Software Engineering",
            "PR Advanced Software Engineering", "SE Advanced Model Engineering", "VU Advanced Model Engineering"
        );

        List<Integer> top5OfferedAmount = Arrays.asList(4, 4, 4, 3, 3);
        List<Integer> top5NeededAmount = Arrays.asList(4, 4, 4, 3, 3);
        assertAll("Statistics",
            () -> assertEquals(5, returnedStatistics.getTopXofferedSubjects().size()),
            () -> assertEquals(5, returnedStatistics.getTopXneededSubjects().size()),
            () -> assertEquals(top5OfferedAmount, returnedStatistics.getTopXofferedAmount()),
            () -> assertEquals(top5NeededAmount, returnedStatistics.getTopXneededAmount()),
            () -> assertThat(returnedStatistics.getTopXofferedSubjects(), containsInAnyOrder(top5OfferedSubjects.toArray())),
            () -> assertThat(returnedStatistics.getTopXneededSubjects(), containsInAnyOrder(top5NeededSubjects.toArray()))
        );
    }

    @Test
    void testBanUser() throws Exception {
        String token = loginAsAdmin();
        var banReason = new BanReasonDto();
        banReason.setReason("TestReason");
        String body = objectMapper.writeValueAsString(banReason);
        Long id = subjectRepository.findAll().get(0).getId();

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/admin/users/" + id + "/ban")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();

        assertNotNull(banRepository.getBanByUserId(id));
        assertEquals("TestReason", banRepository.getBanByUserId(id).getReason());
    }

    @Test
    void testBanUser_ShouldFailWith400() throws Exception {
        String token = loginAsAdmin();
        var banReason = new BanReasonDto();
        String body = objectMapper.writeValueAsString(banReason);
        Long id = subjectRepository.findAll().get(0).getId();

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/admin/users/" + id + "/ban")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void testGetBanUserInfo() throws Exception {
        var id = userRepository.findAll().stream().filter(item -> item.isBanned()).findFirst().get().getId();
        var expectedBan = banRepository.getBanByUserId(id);

        String token = loginAsAdmin();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/users/" + id + "/ban")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        var banDetails = objectMapper.readValue(responseBody, UserBanDetailsDto.class);
        assertAll("BannedUser",
            () -> assertEquals(expectedBan.getReason(), banDetails.getReason()),
            () -> assertEquals(expectedBan.getBanDate(), banDetails.getBanDate()),
            () -> assertEquals(id, banDetails.getId())
        );
    }
}
