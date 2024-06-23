package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentSubjectsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectsListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserMatchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserRating;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubjectKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.RatingRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_EMAIL;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.DEFAULT_USER_EMAIL;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_BASE_URI;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.BANNED_USER_EMAIL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
@AutoConfigureMockMvc
public class UserEndpointTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSubjectRepository userSubjectRepository;

    @Autowired
    private SubjectRepository subjectRepository;

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
        ApplicationUser user = new ApplicationUser("password", false, "Konsti", "U", 123465L, new ContactDetails("+438881919190", "konsti@tuwien.ac.at", new Address("Teststraße 2", 1100, "Wien")), false);
        StudentDto studentDto = userMapper.applicationUserToDto(user);
        String body = objectMapper.writeValueAsString(studentDto);

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

        StudentDto createdStudentDto = objectMapper.readValue(response.getContentAsString(),
            StudentDto.class);

        assertAll(
            () -> assertNotNull(createdStudentDto),
            () -> assertEquals(createdStudentDto.getEmail(), studentDto.getEmail()),
            () -> assertEquals(createdStudentDto.getFirstname(), studentDto.getFirstname()),
            () -> assertEquals(createdStudentDto.getLastname(), studentDto.getLastname()),
            () -> assertEquals(createdStudentDto.getMatrNumber(), studentDto.getMatrNumber()),
            () -> assertTrue(passwordEncoder.matches(studentDto.getPassword(), createdStudentDto.getPassword()))
        );

    }

    @Test
    public void createUserWithExistingEmailShouldFailWith422() throws Exception {
        StudentDto studentDto = new StudentDto();
        studentDto.setPassword("Password123");
        studentDto.setFirstname("test");
        studentDto.setLastname("test");
        studentDto.setMatrNumber(12345678L);
        studentDto.setEmail(DEFAULT_USER_EMAIL);
        String body = objectMapper.writeValueAsString(studentDto);

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isUnprocessableEntity())
            .andDo(print())
            .andReturn();

    }

    @Test
    public void createNewInvalidUser_422() throws Exception {
        ApplicationUser user = new ApplicationUser("", false, "", "", 123465L, new ContactDetails("+438881919190", "konsti@tuswien.ac.at", new Address("Teststraße 2", 1200, "Wien")), false);
        StudentDto studentDto = userMapper.applicationUserToDto(user);
        String body = objectMapper.writeValueAsString(studentDto);

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
        subjectsListDto.traineeSubjects.add(subjectRepository.findAll().get(0).getId());
        subjectsListDto.traineeSubjects.add(subjectRepository.findAll().get(1).getId());
        subjectsListDto.tutorSubjects = new ArrayList<>();
        subjectsListDto.tutorSubjects.add(subjectRepository.findAll().get(2).getId());
        subjectsListDto.tutorSubjects.add(subjectRepository.findAll().get(3).getId());

        String body = objectMapper.writeValueAsString(subjectsListDto);

        MvcResult mvcResult = this.mockMvc.perform(put(USER_BASE_URI + "/subjects")
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
    public void putInvalidSubjectsForUserSubject_Returns422() throws Exception {

        SubjectsListDto subjectsListDto = new SubjectsListDto();
        subjectsListDto.traineeSubjects = new ArrayList<>();
        subjectsListDto.traineeSubjects.add(subjectRepository.findAll().get(0).getId());
        subjectsListDto.traineeSubjects.add(subjectRepository.findAll().get(1).getId());
        subjectsListDto.tutorSubjects = new ArrayList<>();
        subjectsListDto.tutorSubjects.add(subjectRepository.findAll().get(1).getId());
        subjectsListDto.tutorSubjects.add(subjectRepository.findAll().get(2).getId());

        String body = objectMapper.writeValueAsString(subjectsListDto);

        MvcResult mvcResult = this.mockMvc.perform(put(USER_BASE_URI + "/subjects")
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
    void testGetSubjectsOfUser() throws Exception {
        // Retrieve all users and get the ID of the first user
        List<ApplicationUser> users = userRepository.findAll();
        var expectedUser = users.get(0);
        long[] expectedUserSubjects = userSubjectRepository.findAll()
            .stream()
            .filter(item -> item.getUser().getId().equals(expectedUser.getId()))
            .map(item -> item.getSubject().getId())
            .mapToLong(Long::longValue)
            .toArray();
        // Perform a GET request to the "/api/v1/user/{id}/subjects" endpoint
        MvcResult result = mockMvc.perform(get("/api/v1/user/" + expectedUser.getId() + "/subjects")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(users.getFirst().getDetails().getEmail(), USER_ROLES))
            )
            .andExpect(status().isOk())
            .andReturn();

        // Parse the response
        StudentSubjectsDto returnedUser = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), StudentSubjectsDto.class);
        long[] returnedUserSubjects = returnedUser.getSubjects().stream().map(SubjectDto::getId).mapToLong(Long::longValue)
            .toArray();

        // Assert that the returned user has the updated details
        assertAll(
            () -> assertEquals(expectedUser.getFirstname(), returnedUser.getFirstname()),
            () -> assertEquals(expectedUser.getLastname(), returnedUser.getLastname()),
            () -> assertEquals(expectedUser.getMatrNumber(), returnedUser.getMatrNumber()),
            () -> assertEquals(expectedUser.getDetails().getEmail(), returnedUser.getEmail()),
            () -> assertEquals(expectedUser.getDetails().getTelNr(), returnedUser.getTelNr()),
            () -> assertEquals(expectedUser.getDetails().getAddress().getStreet(), returnedUser.getStreet()),
            () -> assertEquals(expectedUser.getDetails().getAddress().getAreaCode(), returnedUser.getAreaCode()),
            () -> assertEquals(expectedUser.getDetails().getAddress().getCity(), returnedUser.getCity()),
            () -> assertArrayEquals(expectedUserSubjects, returnedUserSubjects)
        );
    }

    @Test
    void getMatchingWithoutUserTokenReturns403() throws Exception {
        ArrayList<UserMatchDto> expectedMatches = new ArrayList<>();
        expectedMatches.add(UserMatchDto.builder()
            .firstname("User2")
            .lastname("Surname2")
            .traineeMatchingcount(3)
            .tutorMatchingcount(3)
            .totalMatchingcount(6)
            .traineeSubjects("188.952 Advanced Model Engineering, 188.953 Advanced Model Engineering, 194.056 Advanced Modeling and Simulation")
            .tutorSubjects("183.130 3D Vision, 194.163 AKNUM Reinforcement Learning, 194.160 Abstrakte Maschinen")
            .build()
        );

        expectedMatches.add(UserMatchDto.builder()
            .firstname("User4")
            .lastname("Surname4")
            .traineeMatchingcount(1)
            .tutorMatchingcount(1)
            .totalMatchingcount(2)
            .traineeSubjects("194.056 Advanced Modeling and Simulation")
            .tutorSubjects("194.160 Abstrakte Maschinen")
            .build()
        );


        // Perform a GET request to the "/api/v1/user/matches" endpoint
        mockMvc.perform(get("/api/v1/user/matches"))
            .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateUserWithoutUserTokenReturns403() throws Exception {

        // Create an ApplicationUserDto object with the updated user details
        StudentDto updatedUser = new StudentDto();
        updatedUser.setFirstname("UserUpdated");
        updatedUser.setLastname("SurnameUpdated");
        updatedUser.setPassword("NewPassword123");
        updatedUser.setTelNr("+4367675553");
        updatedUser.setStreet("newStreet 54");
        updatedUser.setAreaCode(1110);
        updatedUser.setCity("Graz");


        mockMvc.perform(put("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateUserWithAdminTokenReturns403() throws Exception {
        // Create an ApplicationUserDto object with the updated user details
        StudentDto updatedUser = new StudentDto();
        updatedUser.setFirstname("UserUpdated");
        updatedUser.setLastname("SurnameUpdated");
        updatedUser.setPassword("NewPassword123");
        updatedUser.setTelNr("+4367675553");
        updatedUser.setStreet("newStreet 54");
        updatedUser.setAreaCode(1110);
        updatedUser.setCity("Graz");


        mockMvc.perform(put("/api/v1/user")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_EMAIL, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isForbidden());
    }

    @Test
    void testGetSubjectsByTokenEmailOfUser() throws Exception {
        Pageable pageable = Pageable.unpaged();
        var expectedUser = userRepository.findAllByFullnameOrMatrNumber(null, 10000001L, null, pageable).getContent().get(0);

        long[] expectedUserSubjects = userSubjectRepository.findAll()
            .stream()
            .filter(item -> item.getUser().getId().equals(expectedUser.getId()))
            .map(item -> item.getSubject().getId())
            .mapToLong(Long::longValue)
            .toArray();
        // Perform a GET request to the "/api/v1/user/{id}/subjects" endpoint
        MvcResult result = mockMvc.perform(get("/api/v1/user/subjects")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(expectedUser.getDetails().getEmail(), USER_ROLES))
            )
            .andExpect(status().isOk())
            .andReturn();

        // Parse the response
        StudentSubjectsDto returnedUser = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), StudentSubjectsDto.class);
        long[] returnedUserSubjects = returnedUser.getSubjects().stream().map(SubjectDto::getId).mapToLong(Long::longValue)
            .toArray();

        // Assert that the returned user has the updated details
        assertAll(
            () -> assertEquals(expectedUser.getFirstname(), returnedUser.getFirstname()),
            () -> assertEquals(expectedUser.getLastname(), returnedUser.getLastname()),
            () -> assertEquals(expectedUser.getMatrNumber(), returnedUser.getMatrNumber()),
            () -> assertEquals(expectedUser.getDetails().getEmail(), returnedUser.getEmail()),
            () -> assertEquals(expectedUser.getDetails().getTelNr(), returnedUser.getTelNr()),
            () -> assertEquals(expectedUser.getDetails().getAddress().getStreet(), returnedUser.getStreet()),
            () -> assertEquals(expectedUser.getDetails().getAddress().getAreaCode(), returnedUser.getAreaCode()),
            () -> assertEquals(expectedUser.getDetails().getAddress().getCity(), returnedUser.getCity()),
            () -> assertArrayEquals(expectedUserSubjects, returnedUserSubjects)
        );
    }

    @Test
    void testUpdateUser() throws Exception {
        // Create an ApplicationUserDto object with the updated user details
        StudentDto updatedUser = new StudentDto();
        updatedUser.setFirstname("UserUpdated");
        updatedUser.setLastname("SurnameUpdated");
        updatedUser.setPassword("NewPassword123");
        updatedUser.setTelNr("+4367675553");
        updatedUser.setStreet("newStreet 54");
        updatedUser.setAreaCode(1110);
        updatedUser.setCity("Graz");
        // Retrieve all users and get the ID of the first user
        List<ApplicationUser> users = userRepository.findAll();

        // Perform a PUT request to the "/api/v1/user" endpoint
        MvcResult result = mockMvc.perform(put("/api/v1/user")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(users.getFirst().getDetails().getEmail(), USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isOk())
            .andReturn();

        // Parse the response
        StudentDto returnedUser = objectMapper.readValue(result.getResponse().getContentAsString(), StudentDto.class);

        // Assert that the returned user has the updated details
        assertAll(
            () -> assertEquals(updatedUser.getFirstname(), returnedUser.getFirstname()),
            () -> assertEquals(updatedUser.getLastname(), returnedUser.getLastname()),
            () -> assertEquals(updatedUser.getTelNr(), returnedUser.getTelNr()),
            () -> assertEquals(updatedUser.getStreet(), returnedUser.getStreet()),
            () -> assertEquals(updatedUser.getAreaCode(), returnedUser.getAreaCode()),
            () -> assertEquals(updatedUser.getCity(), returnedUser.getCity())
        );
    }

    @Test
    void testUserVerificationEndpointSetsUserToVerified() throws Exception {
        List<ApplicationUser> usersList = userRepository.findAll();
        ApplicationUser userBefore = usersList.stream().filter(item -> !item.getVerified()).toList().getFirst();
        assertFalse(userBefore.getVerified());
        String token = jwtTokenizer.buildVerificationToken(userBefore.getDetails().getEmail());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/verify/" + token))
            .andExpect(status().isOk())
            .andReturn();
        ApplicationUser userAfter = userRepository.findById(userBefore.getId()).orElse(null);
        assertTrue(userAfter.getVerified());
    }


    @Test
    void testGetMatchingsShouldReturn2Matches() throws Exception {
        ArrayList<UserMatchDto> expectedMatches = new ArrayList<>();
        expectedMatches.add(UserMatchDto.builder()
            .firstname("Tom")
            .lastname("Clark")
            .traineeMatchingcount(4)
            .tutorMatchingcount(4)
            .totalMatchingcount(8)
            .traineeSubjects("188.953 Advanced Model Engineering, 194.056 Advanced Modeling and Simulation, 183.243 Advanced Software Engineering, 188.910 Advanced Software Engineering")
            .tutorSubjects("183.130 3D Vision, 194.163 AKNUM Reinforcement Learning, 194.160 Abstrakte Maschinen, 188.980 Advanced Information Retrieval")
            .build()
        );

        expectedMatches.add(UserMatchDto.builder()
            .firstname("Olivia")
            .lastname("Hill")
            .traineeMatchingcount(2)
            .tutorMatchingcount(2)
            .totalMatchingcount(4)
            .traineeSubjects("183.243 Advanced Software Engineering, 188.910 Advanced Software Engineering")
            .tutorSubjects("194.160 Abstrakte Maschinen, 188.980 Advanced Information Retrieval")
            .build()
        );


        // Perform a GET request to the "/api/v1/user/matches" endpoint
        var body = mockMvc.perform(get("/api/v1/user/matches")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_EMAIL, USER_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        var matchesResult = objectMapper.readerFor(UserMatchDto.class).readValues(body);
        assertNotNull(matchesResult);

        var matches = new ArrayList<UserMatchDto>();
        matchesResult.forEachRemaining((match) -> matches.add((UserMatchDto) match));
        assertAll(
            () -> assertEquals(2, matches.size()),
            () -> {
                for (int i = 0; i < matches.size(); i++) {
                    UserMatchDto expectedMatch = expectedMatches.get(i);
                    UserMatchDto actualMatch = matches.get(i);

                    assertAll(
                        () -> assertEquals(expectedMatch.getFirstname(), actualMatch.getFirstname()),
                        () -> assertEquals(expectedMatch.getLastname(), actualMatch.getLastname()),
                        () -> assertEquals(expectedMatch.getTraineeMatchingcount(), actualMatch.getTraineeMatchingcount()),
                        () -> assertEquals(expectedMatch.getTutorMatchingcount(), actualMatch.getTutorMatchingcount()),
                        () -> assertEquals(expectedMatch.getTotalMatchingcount(), actualMatch.getTotalMatchingcount()),
                        () -> assertEquals(expectedMatch.getTraineeSubjects(), actualMatch.getTraineeSubjects()),
                        () -> assertEquals(expectedMatch.getTutorSubjects(), actualMatch.getTutorSubjects()),
                        () -> assertEquals(8, actualMatch.getAmount())
                    );
                }
            }
        );
    }

    @Test
    void updatedVisibilityAndGetOneMatchLessReturns200() throws Exception {
        var body = mockMvc.perform(put("/api/v1/user/visibility")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("e10000002@student.tuwien.ac.at", USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(false)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        var booleanBody = mockMvc.perform(get("/api/v1/user/visibility")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("e10000002@student.tuwien.ac.at", USER_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        var visibilityForUser2 = objectMapper.readerFor(boolean.class).readValues(booleanBody);
        assertNotNull(visibilityForUser2);
        assertEquals(false, visibilityForUser2.next());

        body = mockMvc.perform(get("/api/v1/user/matches")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_EMAIL, USER_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        var matchesResult = objectMapper.readerFor(UserMatchDto.class).readValues(body);
        assertNotNull(matchesResult);

        var matches = new ArrayList<UserMatchDto>();
        matchesResult.forEachRemaining((match) -> matches.add((UserMatchDto) match));
        assertAll(
            () -> assertEquals(1, matches.size()));
    }


    @Test
    void testGetMatchingShouldNotReturnBannedUser() throws Exception {
        var allUserSubjects = userSubjectRepository.findAll();
        var matchUser = allUserSubjects.getFirst().getUser();
        var tutorSubject = allUserSubjects.stream().filter(item -> item.getRole().equals("tutor") && item.getUser().equals(matchUser)).findFirst().get();
        var traineeSubject = allUserSubjects.stream().filter(item -> item.getRole().equals("trainee") && item.getUser().equals(matchUser)).findFirst().get();

        var bannedUser = userRepository.findApplicationUserByDetails_Email(BANNED_USER_EMAIL);
        userSubjectRepository.save(new UserSubject(new UserSubjectKey(bannedUser.getId(), tutorSubject.getSubject().getId()), bannedUser, tutorSubject.getSubject(), "trainee"));
        userSubjectRepository.save(new UserSubject(new UserSubjectKey(bannedUser.getId(), traineeSubject.getSubject().getId()), bannedUser, traineeSubject.getSubject(), "tutor"));


        // Perform a GET request to the "/api/v1/user/matches" endpoint
        var body = mockMvc.perform(get("/api/v1/user/matches")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(matchUser.getDetails().getEmail(), USER_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        var matchesResult = objectMapper.readerFor(UserMatchDto.class).readValues(body);
        assertNotNull(matchesResult);

        var matchIds = new ArrayList<Long>();
        matchesResult.forEachRemaining((match) -> {
            var userMatch = (UserMatchDto) match;
            matchIds.add(userMatch.getId());
        });
        assertAll(
            () -> assertEquals(2, matchIds.size()),
            () -> assertFalse(matchIds.contains(bannedUser.getId()))
        );
    }
}
