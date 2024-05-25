package at.ac.tuwien.sepr.groupphase.backend.unittests.email;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.service.email.EmailSmtpService;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.icegreen.greenmail.util.GreenMailUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
public class EmailSmtpServiceTest extends BaseTest {

    @Resource
    private EmailSmtpService emailService;

    private GreenMail greenMail;

    @Before
    public void startMailServer() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    @After
    public void stopMailServer() {
        greenMail.stop();
    }

    @Test
    public void testVerificationEmailSent()
        throws InterruptedException, MessagingException {

        String mailSubject = "TutorMatch - Verify your email";
        String mailTo = "test@excaple.com";
        CreateStudentDto testDto = new CreateStudentDto();

        testDto.setEmail(mailTo);
        testDto.setFirstname("Max");
        testDto.setLastname("Mustermann");
        emailService.sendVerificationEmail(testDto);

        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(mailTo);
        assertEquals(1, messages.length);
        assertEquals(mailSubject, messages[0].getSubject());
        String body = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", "");
        assertThat(body, containsString("Max Mustermann"));
    }
}
