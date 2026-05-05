package com.college.eventapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String resetBaseUrl;

    public MailServiceImpl(JavaMailSender mailSender,
                           @Value("${app.password-reset.from-address:${spring.mail.username:}}") String fromAddress,
                           @Value("${app.password-reset.base-url:http://localhost:8080/reset-password}") String resetBaseUrl) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.resetBaseUrl = resetBaseUrl;
    }

    @Override
    public void sendPasswordResetEmail(String recipientEmail, String recipientName, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            if (fromAddress != null && !fromAddress.isBlank()) {
                helper.setFrom(fromAddress);
            }
            helper.setTo(recipientEmail);
            helper.setSubject("EventApp Password Reset");
            helper.setText(buildBody(recipientName, token), false);
            mailSender.send(message);
        } catch (MailException | MessagingException ex) {
            log.error("Failed to send password reset email to {}", recipientEmail, ex);
        }
    }

    private String buildBody(String recipientName, String token) {
        String resolvedName = recipientName == null || recipientName.isBlank() ? "there" : recipientName;
        String resetLink = resetBaseUrl + "?token=" + token;
        return """
                Hello %s,

                We received a request to reset your EventApp password.

                Reset link:
                %s

                Reset token:
                %s

                This token expires in 15 minutes and can be used only once.

                If you did not request this, you can ignore this email.
                """.formatted(resolvedName, resetLink, token);
    }
}
