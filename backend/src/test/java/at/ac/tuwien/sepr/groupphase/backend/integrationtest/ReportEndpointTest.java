package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReportChatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReportDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ChatRoom;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.helper.RestResponsePage;
import at.ac.tuwien.sepr.groupphase.backend.repository.BanRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatRoomRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
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

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.BASE_URI;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_BASE_URI;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_ROLES;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
@AutoConfigureMockMvc
public class ReportEndpointTest {

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private ChatRoomRepository chatRoomRepository;


    @Test
    public void ValidReportUser2Returns201() throws Exception {
        List<ApplicationUser> users = userRepository.findAll();
        this.mockMvc.perform(post(BASE_URI + "/report/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(users.getFirst().getDetails().getEmail(), USER_ROLES))
                .content(objectMapper.writeValueAsString("is very annoying")))
            .andExpect(status().isCreated())
            .andReturn();
    }

    @Test
    public void ValidReportFeedbackReturns201() throws Exception {
        List<ApplicationUser> users = userRepository.findAll();
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI + "/report/feedback/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(users.get(1).getDetails().getEmail(), USER_ROLES))
                .content(objectMapper.writeValueAsString("is very annoying")))
            .andExpect(status().isCreated())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                assertEquals(0, content.length());
            }
        );
    }
    @Test
    public void ValidReportChatReturns201() throws Exception {
        List<ApplicationUser> users = userRepository.findAll();
        List<ChatRoom> c = chatRoomRepository.findAllBySenderId(users.get(1).getId());
        ReportChatDto reportChatDto = new ReportChatDto();
        reportChatDto.setChatId(c.getFirst().getChatRoomId());
        reportChatDto.setReason("is very annoying");
        this.mockMvc.perform(post(BASE_URI + "/report/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(users.get(1).getDetails().getEmail(), USER_ROLES))
                .content(objectMapper.writeValueAsString(reportChatDto)))
            .andExpect(status().isCreated())
            .andReturn();
    }
    @Test
    public void GetAllReportsShouldReturnListOf3Reports() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/report")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("test@admin.at", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ReportDto> reportDtos = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertEquals(3, reportDtos.size());
        reportDtos.removeLast();
        String expectedResponse= "[ReportDto(firstnameReported=User2, lastNameReported=Surname2, reportedId=2, reporterId=1, reason=Test reason!!, id=1, firstnameReporter=User1, lastnameReporter=Surname1, feedback=Feedback from user1 to user 2, chatRoomId=), ReportDto(firstnameReported=User2, lastNameReported=Surname2, reportedId=2, reporterId=1, reason=Report user, id=2, firstnameReporter=User1, lastnameReporter=Surname1, feedback=, chatRoomId=)]";
        assertEquals(expectedResponse, reportDtos.toString());
    }
}
