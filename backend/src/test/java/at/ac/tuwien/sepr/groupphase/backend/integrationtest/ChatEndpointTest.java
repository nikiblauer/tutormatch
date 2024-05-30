package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateChatRoomDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserMatchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserSubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
@AutoConfigureMockMvc
public class ChatEndpointTest extends BaseTest{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Test
    public void testCreateChatRoomWithValidTokenReturnsChatRoomDto() throws Exception {
        Pageable pageable = Pageable.unpaged();
        var recipient = userRepository.findAllByFullnameOrMatrNumber(null, 10000002L, pageable).getContent().get(0);

        var sender = userRepository.findAllByDetails_Email(DEFAULT_USER_EMAIL).get(0);

        CreateChatRoomDto chatRoomDto = new CreateChatRoomDto(recipient.getId());

        MvcResult mvcResult = this.mockMvc.perform(post(CHAT_BASE_URI+"/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRoomDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_EMAIL, USER_ROLES)))
            .andExpect(status().isCreated())
            .andReturn();


        ChatRoomDto returnedChatRoomDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ChatRoomDto.class);
        assertNotNull(returnedChatRoomDto);

        String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";
        Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);

        assertAll(
            () -> assertTrue(UUID_PATTERN.matcher(returnedChatRoomDto.getChatRoomId()).matches()),
            () -> assertEquals(recipient.getId(), returnedChatRoomDto.getRecipientId()),
            () -> assertEquals(sender.getId(), returnedChatRoomDto.getSenderId())
        );
    }

    @Test
    public void testCreateChatRoomWithInValidTokenReturns403() throws Exception {
        Pageable pageable = Pageable.unpaged();
        var recipient = userRepository.findAllByFullnameOrMatrNumber(null, 10000002L, pageable).getContent().get(0);

        CreateChatRoomDto chatRoomDto = new CreateChatRoomDto(recipient.getId());

        this.mockMvc.perform(post(CHAT_BASE_URI+"/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRoomDto)))
            .andExpect(status().isForbidden())
            .andReturn();
    }


    @Test
    public void testGetChatRooms() throws Exception {

        ApplicationUser user1 = userRepository.findApplicationUserByDetails_Email("e10000001@student.tuwien.ac.at");
        ApplicationUser user3 = userRepository.findApplicationUserByDetails_Email("e10000003@student.tuwien.ac.at");
        ApplicationUser user4 = userRepository.findApplicationUserByDetails_Email("e10000004@student.tuwien.ac.at");

        ArrayList<ChatRoomDto> expectedChatRooms = new ArrayList<>();
        expectedChatRooms.add(ChatRoomDto.builder()
                .chatRoomId("123e4567-e89b-12d3-a456-426614174000")
                .senderId(user1.getId())
                .recipientId(user3.getId())
            .build()
        );
        expectedChatRooms.add(ChatRoomDto.builder()
            .chatRoomId("321e4567-e89b-12d3-a456-426614174000")
            .senderId(user1.getId())
            .recipientId(user4.getId())
            .build()
        );


        var body = this.mockMvc.perform(get(CHAT_BASE_URI+"/room/user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_EMAIL, USER_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        var chatRoomsResult = objectMapper.readerFor(ChatRoomDto.class).readValues(body);
        assertNotNull(chatRoomsResult);

        var chatrooms = new ArrayList<ChatRoomDto>();
        chatRoomsResult.forEachRemaining((chatroom) -> chatrooms.add((ChatRoomDto) chatroom));

        assertAll(
            () -> assertEquals(2, chatrooms.size()),
            () -> {
                for (int i = 0; i < chatrooms.size(); i++) {
                    ChatRoomDto expectedMatch = expectedChatRooms.get(i);
                    ChatRoomDto actualMatch = chatrooms.get(i);


                    assertAll(
                        () -> assertEquals(expectedMatch.getChatRoomId(), actualMatch.getChatRoomId()),
                        () -> assertEquals(expectedMatch.getSenderId(), actualMatch.getSenderId()),
                        () -> assertEquals(expectedMatch.getRecipientId(), actualMatch.getRecipientId())
                    );
                }
            }
        );
    }



    @Test
    public void testGetChatRoomsByUserId() throws Exception {
        ApplicationUser user1 = userRepository.findApplicationUserByDetails_Email("e10000001@student.tuwien.ac.at");
        ApplicationUser user3 = userRepository.findApplicationUserByDetails_Email("e10000003@student.tuwien.ac.at");
        ApplicationUser user4 = userRepository.findApplicationUserByDetails_Email("e10000004@student.tuwien.ac.at");

        ArrayList<ChatRoomDto> expectedChatRooms = new ArrayList<>();
        expectedChatRooms.add(ChatRoomDto.builder()
            .chatRoomId("123e4567-e89b-12d3-a456-426614174000")
            .senderId(user1.getId())
            .recipientId(user3.getId())
            .build()
        );
        expectedChatRooms.add(ChatRoomDto.builder()
            .chatRoomId("321e4567-e89b-12d3-a456-426614174000")
            .senderId(user1.getId())
            .recipientId(user4.getId())
            .build()
        );


        var body = this.mockMvc.perform(get(CHAT_BASE_URI+"/room/user/" + user1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_EMAIL, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        var chatRoomsResult = objectMapper.readerFor(ChatRoomDto.class).readValues(body);
        assertNotNull(chatRoomsResult);

        var chatrooms = new ArrayList<ChatRoomDto>();
        chatRoomsResult.forEachRemaining((chatroom) -> chatrooms.add((ChatRoomDto) chatroom));

        assertAll(
            () -> assertEquals(2, chatrooms.size()),
            () -> {
                for (int i = 0; i < chatrooms.size(); i++) {
                    ChatRoomDto expectedMatch = expectedChatRooms.get(i);
                    ChatRoomDto actualMatch = chatrooms.get(i);


                    assertAll(
                        () -> assertEquals(expectedMatch.getChatRoomId(), actualMatch.getChatRoomId()),
                        () -> assertEquals(expectedMatch.getSenderId(), actualMatch.getSenderId()),
                        () -> assertEquals(expectedMatch.getRecipientId(), actualMatch.getRecipientId())
                    );
                }
            }
        );
    }



    @Test
    public void testGetMessagesByChatRoomId() throws Exception {
        ApplicationUser user1 = userRepository.findApplicationUserByDetails_Email("e10000001@student.tuwien.ac.at");
        ApplicationUser user3 = userRepository.findApplicationUserByDetails_Email("e10000003@student.tuwien.ac.at");

        // Convert LocalDate to Date
        LocalDateTime dateTime1 = LocalDateTime.of(2023, 1, 1, 0, 0);

        // Add 1 minute to the first date to get the second date
        LocalDateTime dateTime2 = dateTime1.plusMinutes(1);

        // Convert LocalDateTime to Date
        Date timestampMsg1 = Date.from(dateTime1.atZone(ZoneId.systemDefault()).toInstant());
        Date timestampMsg2 = Date.from(dateTime2.atZone(ZoneId.systemDefault()).toInstant());

        ArrayList<ChatMessageDto> expectedChatMessages = new ArrayList<>();
        expectedChatMessages.add(ChatMessageDto.builder()
            .chatRoomId("123e4567-e89b-12d3-a456-426614174000")
            .senderId(user1.getId())
            .recipientId(user3.getId())
            .content("Hi, how are you?")
            .timestamp(timestampMsg1)
            .build()
        );
        expectedChatMessages.add(ChatMessageDto.builder()
            .chatRoomId("123e4567-e89b-12d3-a456-426614174000")
            .senderId(user3.getId())
            .recipientId(user1.getId())
            .content("I'm fine. How are you?")
            .timestamp(timestampMsg2)
            .build()
        );



        var body = this.mockMvc.perform(get(CHAT_BASE_URI+"/room/123e4567-e89b-12d3-a456-426614174000/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_EMAIL, USER_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        var chatMessagesResult = objectMapper.readerFor(ChatMessageDto.class).readValues(body);
        assertNotNull(chatMessagesResult);

        var chatMessages = new ArrayList<ChatMessageDto>();
        chatMessagesResult.forEachRemaining((chatroom) -> chatMessages.add((ChatMessageDto) chatroom));

        assertAll(
            () -> assertEquals(2, chatMessages.size()),
            () -> {
                for (int i = 0; i < chatMessages.size(); i++) {
                    ChatMessageDto expectedMatch = expectedChatMessages.get(i);
                    ChatMessageDto actualMatch = chatMessages.get(i);


                    assertAll(
                        () -> assertEquals(expectedMatch.getChatRoomId(), actualMatch.getChatRoomId()),
                        () -> assertEquals(expectedMatch.getSenderId(), actualMatch.getSenderId()),
                        () -> assertEquals(expectedMatch.getRecipientId(), actualMatch.getRecipientId()),
                        () -> assertEquals(expectedMatch.getContent(), actualMatch.getContent()),
                        () -> assertEquals(expectedMatch.getTimestamp(), actualMatch.getTimestamp())
                    );
                }
            }
        );

    }



}
