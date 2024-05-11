package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserMatchDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
@AutoConfigureMockMvc
public class UserMatchServiceTest extends BaseTest {


    @Test
    void testFindMatchingUserByUserIdShouldReturn2Results() {
        var user = userRepository.findAllByFullnameOrMatrNumber(null, 10000001L);
        var stream = userMatchService.findMatchingUserByUserIdAsStream(user.get(0).getId());

        List<UserMatchDto> resultList = stream.collect(Collectors.toList());
        assertEquals(2, resultList.size());
    }
}
