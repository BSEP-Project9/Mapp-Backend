package com.example.Mapp.service;

import com.example.Mapp.confirmation.EmailConfirmationToken;
import com.example.Mapp.confirmation.EmailConfirmationTokenService;
import com.example.Mapp.confirmation.HmacUtil;
import com.example.Mapp.model.User;
import com.example.Mapp.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailService{

     private final JavaMailSender emailSender;
     private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
     private final UserRepository userRepository;

     private final EmailConfirmationTokenService emailConfirmationTokenService;

     private final HmacUtil hmacUtil;

     @Async
     public void sendActivationEmail(User user) throws NoSuchAlgorithmException, InvalidKeyException {
         //String link = "http://localhost:8040/api/auth/check-email/confirm?token=" + token;

         //String token = UUID.randomUUID().toString();
         String token = "db92f272-d030-4c54-8d66-338f0a187c51";
         System.out.println("OVO JE RANDOM UUID: " + token);
         EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
                 token,
                 LocalDateTime.now(),
                 LocalDateTime.now().plusMinutes(10),
                 user.getId()
         );
         emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);

         String secretKey = "tajni_kljuc";

         String potpisanToken = hmacUtil.signToken(token, secretKey);
         System.out.println("Hmac token: " + potpisanToken);
         //String encodedToken = URLEncoder.encode(potpisanToken, StandardCharsets.UTF_8);
         String encodedToken = Base64.getUrlEncoder().encodeToString(potpisanToken.getBytes(StandardCharsets.UTF_8));
         System.out.println("Zaglavnje: " + encodedToken);


         String link = "http://localhost:4200/login-redirect/confirm?token=" + encodedToken +"&email="+user.getEmail();
         try {
             MimeMessage mimeMessage = emailSender.createMimeMessage();
             MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
             String htmlContent = "<h1>Please verify link</h1>"+" <p>Hello, welcome to your <a href=\""+link+"\"> account <a></p>";
             messageHelper.setText(htmlContent, true);
             messageHelper.setTo("praksaproba1@gmail.com");
             messageHelper.setSubject("New login detected");
             messageHelper.setFrom("praksaproba1@gmail.com");
             emailSender.send(mimeMessage);

         }catch (MessagingException e){
             LOGGER.error("Failed to send email", e);
             throw new IllegalStateException("Failed to send email");
         }
        }

        public User confirmLogin(String token, String email) throws NoSuchAlgorithmException, InvalidKeyException {
         //TO DO: Handle expiration date exception
            byte[] decodedBytes = Base64.getUrlDecoder().decode(token);
            String decodedToken = new String(decodedBytes, StandardCharsets.UTF_8);
            String secretKey = "tajni_kljuc";
            User user = userRepository.findOneByEmail(email);
            if(user != null){
                EmailConfirmationToken emailConfirmationToken = emailConfirmationTokenService.findByUserId(user.getId());
                if(hmacUtil.verifySignature(emailConfirmationToken.getToken(), decodedToken, secretKey)){
                    System.out.println("Token je ispravano potpisan");
                    if(emailConfirmationToken.getExpiredAt().isAfter(LocalDateTime.now())){
                        System.out.println("Token je vremenski ispravan");
                        return user;
                    }

                }

            }
            return null;
        }
}
