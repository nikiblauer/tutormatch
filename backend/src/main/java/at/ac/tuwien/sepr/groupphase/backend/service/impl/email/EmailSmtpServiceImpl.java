package at.ac.tuwien.sepr.groupphase.backend.service.impl.email;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.email.EmailSmtpService;
import at.ac.tuwien.sepr.groupphase.backend.service.email.ThymeleafService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailSmtpServiceImpl implements EmailSmtpService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private JavaMailSender mailSender;

    ThymeleafService thymeleafService;

    private final JwtTokenizer jwtTokenizer;



    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    public EmailSmtpServiceImpl(JwtTokenizer jwtTokenizer, JavaMailSender mailSender, ThymeleafService thymeleafService) {
        this.mailSender = mailSender;
        this.thymeleafService = thymeleafService;
        this.jwtTokenizer = jwtTokenizer;
    }

    @Override
    public void sendVerificationEmail(CreateApplicationUserDto dto) {
        LOG.trace("Send verification email {}", dto);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
            );

            helper.setTo(dto.getEmail());
            helper.setSubject("TutorMatch - Verify your email");

            Map<String, Object> variables = new HashMap<>();
            variables.put("full_name", dto.getFirstname() + " " + dto.getLastname());
            String token = jwtTokenizer.buildVerificationToken(dto.getEmail());
            //TODO link to frontend page here and call backend endpoint with GET request
            variables.put("verification_link", "http://localhost:8080/api/v1/user/verify/" + token);
            helper.setText(thymeleafService.createContent("verification_email.html", variables), true);
            helper.setFrom(senderEmail);
            mailSender.send(message);
        } catch (Exception e) {
            LOG.debug("Send verification email failed", e.getStackTrace());
        }
    }
}
