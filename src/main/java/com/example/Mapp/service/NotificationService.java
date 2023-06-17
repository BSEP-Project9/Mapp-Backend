package com.example.Mapp.service;

import com.example.Mapp.confirmation.EmailConfirmationToken;
import com.example.Mapp.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class NotificationService {

    private final JavaMailSender emailSender;
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public NotificationService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async("threadPoolTaskExecutor")
    public void sendLoggerNotificationEmail(String content){
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            String htmlContent = "<h1>Application warning</h1>"+" <p>"+content+"</p>";
            messageHelper.setText(htmlContent, true);
            messageHelper.setTo("praksaproba1@gmail.com");
            messageHelper.setSubject("Detected warning");
            messageHelper.setFrom("praksaproba1@gmail.com");
            emailSender.send(mimeMessage);
            LOGGER.info("Email send to ADMIN; REASON: logger notification");
        }catch (MessagingException e){
            LOGGER.error("Failed to send email to ADMIN", e);
            throw new IllegalStateException("Failed to send email");
        }
    }
}
