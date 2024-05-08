package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
@AutoConfigureMockMvc
public class AdminEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testQueryUser2Surname2() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/users")
                .param("firstname", "User2")
                .param("lastname", "Surname2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ApplicationUserDto> returnedUsers = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertEquals(1, returnedUsers.size());
        assertEquals("User2", returnedUsers.get(0).getFirstname());
        assertEquals("Surname2", returnedUsers.get(0).getLastname());
    }

    @Test
    void testQueryUnknownUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/admin/users")
                .param("firstname", "asdf")
                .param("lastname", "lasdflalasdf")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ApplicationUserDto> returnedUsers = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertEquals(0, returnedUsers.size()); //empty list
    }
}
