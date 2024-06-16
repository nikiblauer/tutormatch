package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.helper.PageableDeserializer;
import at.ac.tuwien.sepr.groupphase.backend.helper.RestResponsePage;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

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
@Import(SubjectEndpointTest.JacksonConfig.class)
public class SubjectEndpointTest extends BaseTest {

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

    //Use this config only in this Test to deserialize Page<T>
    @TestConfiguration
    static class JacksonConfig {
        @Bean
        @Primary
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            SimpleModule module = new SimpleModule();
            module.addDeserializer(Pageable.class, new PageableDeserializer());
            mapper.registerModule(module);
            return mapper;
        }
    }


    private static Stream<Arguments> provideParameters() {
        return Stream.of(
            Arguments.of("/api/v1/subject?q=xyz", 0),
            Arguments.of("/api/v1/subject?page=0&size=100", 100),
            Arguments.of("/api/v1/subject", 20),
            Arguments.of("/api/v1/subject?q=3D Vision", 2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    void testGetSubjects(String query, int expectedSize) throws Exception {
        // Perform a GET request to the endpoint
        MvcResult result = mockMvc.perform(get(query))
            .andExpect(status().isOk())
            .andReturn();

        // Parse the response
        var returnedSubjects = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
            new TypeReference<RestResponsePage<SubjectDto>>() {});
        assertEquals(expectedSize, returnedSubjects.getContent().size());
    }

    @Test
    void testGetSubjectsShouldReturnSingleSubjects() throws Exception {
        // Perform a GET request to the "/api/v1/user/{id}/subjects" endpoint
        MvcResult result = mockMvc.perform(get("/api/v1/subject?q=VO 183.129 3D Vision"))
            .andExpect(status().isOk())
            .andReturn();

        var returnedSubjects = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
            new TypeReference<RestResponsePage<SubjectDto>>() {});
        assertEquals(1, returnedSubjects.getContent().size());

        var returnedSubject = returnedSubjects.getContent().get(0);
        assertAll(
            () -> assertEquals("VO 183.129 3D Vision", returnedSubject.getName()),
            () -> assertEquals("https://tiss.tuwien.ac.at/course/courseDetails.xhtml?courseNr=183129&semester=2024S", returnedSubject.getUrl()),
            () -> assertEquals("HOURS:2.0, ECTS:3.0", returnedSubject.getDescription())
        );
    }
}

