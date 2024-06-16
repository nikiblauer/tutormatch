package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.repository.FeedbackRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.*;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.BANNED_USER_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
@AutoConfigureMockMvc
public class FeedbackEndpointTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Test
    void deleteFeedbackByIdStudentReturns422WhenRequestUserIdIsNotRatedIdOrRaterId() throws Exception {
        ApplicationUser user = userRepository.findApplicationUserByDetails_Email(DEFAULT_USER_EMAIL);
        Feedback feedback = feedbackRepository.findAll().stream().filter(item -> !Objects.equals(item.getRated(), user.getId()) && !Objects.equals(item.getRater(), user.getId())).findFirst().orElse(null);
        assertNotNull(feedback);
        assertNotEquals(feedback.getRater(), user.getId());
        assertNotEquals(feedback.getRated(), user.getId());
        MvcResult result = mockMvc.perform(
                                delete("/api/v1/feedback/delete/" + feedback.getId())
                               .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_EMAIL, USER_ROLES)))
                               .andExpect(status().isUnprocessableEntity())
                               .andReturn();
        assertNotNull(feedbackRepository.findFeedbackById(feedback.getId()));

    }

    @Test
    void deleteFeedbackByIdStudentReturns200OkWhenRequestUserIdIsRaterId() throws Exception {
        var user = userRepository.findApplicationUserByDetails_Email(DEFAULT_USER_EMAIL);
        Feedback feedback = feedbackRepository.findAllByRater(user.getId()).getFirst();
        assertNotNull(feedback);
        assertEquals(feedback.getRater(),user.getId());

        MvcResult result = mockMvc.perform(
                delete("/api/v1/feedback/delete/" + feedback.getId())
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_EMAIL, USER_ROLES)))
                                   .andExpect(status().isOk())
                                   .andReturn();
        assertNull(feedbackRepository.findFeedbackById(1L));
    }

}
