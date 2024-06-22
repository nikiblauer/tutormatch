package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestUtils;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BanReasonDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CoverageSubjectsStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentSubjectInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TopStatisticsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentAsAdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserBanDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.helper.RestResponsePage;
import at.ac.tuwien.sepr.groupphase.backend.repository.BanRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.tiss.TissClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

    @MockBean
    private TissClient tissClientMock;


    @Test
    void testGetSubjectPreview_ShouldReturnSubject() throws Exception {
        //Mock Tiss response
        Subject mockedSubject = new Subject(); // Create a mock Subject object
        mockedSubject.setType("VU");
        mockedSubject.setUrl("https://tiss.tuwien.ac.at/course/courseDetails.xhtml?courseNr=185A92&semester=2024S");
        mockedSubject.setTitle("Programmierung 2");
        mockedSubject.setNumber("185.A92");
        mockedSubject.setSemester("2024S");
        mockedSubject.setDescription("description");

        when(tissClientMock.getCourseInfo(anyString(), anyString())).thenReturn(mockedSubject);

        String token = loginAsAdmin();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/subject/courses/189189/semesters/2024S/preview")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        SubjectCreateDto subject = objectMapper.readValue(responseBody, SubjectCreateDto.class);
        assertAll(
            () -> assertEquals(mockedSubject.getTitle(), subject.getTitle()),
            () -> assertEquals(mockedSubject.getType(), subject.getType()),
            () -> assertEquals(mockedSubject.getUrl(), subject.getUrl()),
            () -> assertEquals(mockedSubject.getSemester(), subject.getSemester()),
            () -> assertEquals(mockedSubject.getNumber(), subject.getNumber()),
            () -> assertEquals(mockedSubject.getDescription(),subject.getDescription())
        );
    }

    @Test
    void testQueryUser2Surname2() throws Exception {
        var expectedUser = userRepository.findAll().get(0);
        String token = loginAsAdmin();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/users")
                .param("fullname", expectedUser.getFirstname() + " " + expectedUser.getLastname())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        RestResponsePage<StudentDto> returnedPage = objectMapper.readValue(responseBody, new TypeReference<RestResponsePage<StudentDto>>() {
        });
        List<StudentDto> returnedUsers = returnedPage.getContent();

        assertEquals(1, returnedUsers.size());
        assertEquals(expectedUser.getFirstname(), returnedUsers.get(0).getFirstname());
        assertEquals(expectedUser.getLastname(), returnedUsers.get(0).getLastname());
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
        var expectedUser = users.get(0);

        String token = loginAsAdmin();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/users/" + expectedUser.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        StudentSubjectInfoDto returnedUser = objectMapper.readValue(responseBody, StudentSubjectInfoDto.class);

        assertAll("User",
            () -> assertEquals(expectedUser.getFirstname(), returnedUser.getFirstname()),
            () -> assertEquals(expectedUser.getLastname(), returnedUser.getLastname()),
            () -> assertEquals(expectedUser.getDetails().getEmail(), returnedUser.getEmail()),
            () -> assertEquals(expectedUser.getDetails().getTelNr(), returnedUser.getTelNr()),
            () -> assertEquals(expectedUser.getDetails().getAddress().getAreaCode(), returnedUser.getAreaCode()),
            () -> assertEquals(expectedUser.getDetails().getAddress().getCity(), returnedUser.getCity()),
            () -> assertEquals("[Advanced Model Engineering, Advanced Model Engineering, Advanced Modeling and Simulation, Advanced Software Engineering, Advanced Software Engineering, Critical Design]", Arrays.toString(returnedUser.getTutorSubjects())),
            () -> assertEquals("[3D Vision, 3D Vision, AKNUM Reinforcement Learning, Abstrakte Maschinen, Advanced Information Retrieval, Parallel Computing]", Arrays.toString(returnedUser.getTraineeSubjects()))
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
            () -> assertEquals(9, returnedStatistics.getRegisteredVerifiedUsers()),
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

        List<String> expectedOfferedSubjects = Arrays.asList(
            "193.150 VU Critical Design (2024S)",
            "194.056 VU Advanced Modeling and Simulation (2024S)",
            "188.910 PR Advanced Software Engineering (2024S)",
            "188.952 VU Advanced Model Engineering (2024S)",
            "183.243 PR Advanced Software Engineering (2024S)"
        );
        List<String> expectedNeededSubjects = Arrays.asList(
            "184.710 VU Parallel Computing (2024S)",
            "183.243 PR Advanced Software Engineering (2024S)",
            "194.164 SE Advanced Topics in Recommender Systems and Generative AI (2024S)",
            "188.953 SE Advanced Model Engineering (2024S)",
            "188.910 PR Advanced Software Engineering (2024S)"
        );
        List<String> alternativeExpectedNeededSubjects = Arrays.asList(
            "184.710 VU Parallel Computing (2024S)",
            "194.056 VU Advanced Modeling and Simulation (2024S)",
            "188.910 PR Advanced Software Engineering (2024S)",
            "183.243 PR Advanced Software Engineering (2024S)",
            "188.953 SE Advanced Model Engineering (2024S)"
        );

        List<Integer> top5OfferedAmount = Arrays.asList(9, 5, 5, 4, 4);
        List<Integer> top5NeededAmount = Arrays.asList(9, 5, 5, 4, 4);
        assertAll("Statistics",
            () -> assertEquals(5, returnedStatistics.getTopXofferedSubjects().size()),
            () -> assertEquals(5, returnedStatistics.getTopXneededSubjects().size()),
            () -> assertEquals(top5OfferedAmount, returnedStatistics.getTopXofferedAmount()),
            () -> assertEquals(top5NeededAmount, returnedStatistics.getTopXneededAmount()),
            () -> assertThat(returnedStatistics.getTopXofferedSubjects(), containsInAnyOrder(expectedOfferedSubjects.toArray())),
            () -> assertThat(returnedStatistics.getTopXneededSubjects(), anyOf(containsInAnyOrder(expectedNeededSubjects.toArray()), containsInAnyOrder(alternativeExpectedNeededSubjects.toArray())))
        );
    }

    @Test
    public void testGetCoverageSubjectsStatistics() throws Exception {
        // Arrange
        int x = 5;
        String expectedMostRequestedSubject = "184.710 VU Parallel Computing (2024S)";
        String expectedMostOfferedSubject = "193.150 VU Critical Design (2024S)";
        String expectedMostRequestedSubjectAmount = "Trainees: 9, Tutors: 0";
        String expectedMostOfferedSubjectAmount = "Trainees: 9, Tutors: 0";

        // Act
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/statistics/coverage")
                .param("x", String.valueOf(x))
                .header("Authorization", "Bearer " + loginAsAdmin())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        String responseBody = mvcResult.getResponse().getContentAsString();
        CoverageSubjectsStatisticsDto returnedStatistics = objectMapper.readValue(responseBody, CoverageSubjectsStatisticsDto.class);

        assertEquals(expectedMostRequestedSubject, returnedStatistics.getMostRequestedSubjectsWithoutCoverage().get(0));
        assertEquals(expectedMostOfferedSubject, returnedStatistics.getMostOfferedSubjectsWithoutCoverage().get(0));
        assertEquals(expectedMostRequestedSubjectAmount, returnedStatistics.getNumberOfStudentsRequestedSubjects().get(0));
        assertEquals(expectedMostOfferedSubjectAmount, returnedStatistics.getNumberOfStudentsRequestedSubjects().get(0));
    }

    @Test
    void testBanUser() throws Exception {
        String token = loginAsAdmin();
        var banReason = new BanReasonDto();
        banReason.setReason("TestReason");
        String body = objectMapper.writeValueAsString(banReason);
        Long id = userRepository.findAll().get(0).getId();

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
