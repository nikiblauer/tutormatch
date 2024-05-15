package at.ac.tuwien.sepr.groupphase.backend.basetest;


import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;

public class TestUtils {

    public static String validLoginTest(MockMvc mockMvc, String loginData, ArrayList<String> expectedRole, String expectedEmail, SecurityProperties securityProperties) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginData))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String responseBody = response.getContentAsString().trim();
        String token = responseBody.replace("Bearer ", "");

        byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        Claims claims = Jwts.parser()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        ArrayList<String> actualRole = (ArrayList<String>) claims.get("rol");
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals("text/plain;charset=UTF-8", response.getContentType()),
            () -> assertEquals(expectedEmail, claims.get("sub")),
            () -> assertEquals(expectedRole, actualRole)
        );

        return token;
    }
}
