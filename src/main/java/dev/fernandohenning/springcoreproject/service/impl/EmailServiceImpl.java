package dev.fernandohenning.springcoreproject.service.impl;

import dev.fernandohenning.springcoreproject.config.EmailConfigProperties;
import dev.fernandohenning.springcoreproject.service.EmailService;
import io.jsonwebtoken.io.IOException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.util.Calendar;

@Slf4j
@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    private final EmailConfigProperties emailConfigProperties;

    @Override
    public void sendEmailWithTemplate(String email, String firstName, String subject, String url, String template) {
        String senderName = "User services";
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        try{
            MimeMessage message =  javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailConfigProperties.getUsername(), senderName);
            helper.setTo(email);
            helper.setSubject(subject);

            ClassPathResource resource = new ClassPathResource(template);
            String content = new String(Files.readAllBytes(resource.getFile().toPath()));

            content = content.replace("{{firstName}}", firstName);
            content = content.replace("{{activationLink}}", url);
            content = content.replace("{{currentYear}}", currentYear);
            helper.setText(content, true);

            javaMailSender.send(message);

        } catch (MessagingException | IOException | java.io.IOException exception){
            log.error(exception.getMessage());
        }

    }

    @Override
    public void sendActivationLink(String email, String firstName, String activationLink) {
        final String ACTIVATION_EMAIL_TEMPLATE = "templates/activate-account.html";
        String subject = "Activate Your Account";

        sendEmailWithTemplate(email, firstName, subject, activationLink, ACTIVATION_EMAIL_TEMPLATE);
    }

    @Override
    public void sendResetPasswordRequest(String email, String firstName, String resetPasswordLink) {
        final String RESET_PASSWORD_EMAIL_TEMPLATE = "template/reset-password.html";
        String subject = "Reset Your Password";

        sendEmailWithTemplate(email, firstName, subject, resetPasswordLink, RESET_PASSWORD_EMAIL_TEMPLATE);
    }
}
