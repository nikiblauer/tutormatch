package at.ac.tuwien.sepr.groupphase.backend.service.impl.email;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
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

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    ThymeleafService thymeleafService;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendVerificationEmail(ApplicationUserDto dto) {
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
            //TODO create verification link
            //authenticator.createVerificationLink(dto.getEmail())
            variables.put("verification_link", "www.google.com");
            helper.setText(thymeleafService.createContent("verification_email.html", variables), true);
            helper.setFrom(senderEmail);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
