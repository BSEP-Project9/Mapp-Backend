package com.example.Mapp.service;

import com.example.Mapp.confirmation.EmailConfirmationToken;
import com.example.Mapp.confirmation.EmailConfirmationTokenService;
import com.example.Mapp.confirmation.HmacUtil;
import com.example.Mapp.enums.Status;
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

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailService{

     private final JavaMailSender emailSender;
     private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
     private final UserRepository userRepository;

     private final EmailConfirmationTokenService emailConfirmationTokenService;

     private final HmacUtil hmacUtil;

     @Async("threadPoolTaskExecutor")
     public void sendConfirmationEmail(User user) throws NoSuchAlgorithmException, InvalidKeyException {

         String token = UUID.randomUUID().toString();
         //String token = "db92f272-d030-4c54-8d66-338f0a187c51";
         EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
                 token,
                 LocalDateTime.now(),
                 LocalDateTime.now().plusMinutes(2),
                 user.getId(),
                 "CONFIRM_LOGIN",
                 "PENDING"
         );
         emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);

         String potpisanToken = hmacUtil.signToken(token);
         String encodedToken = Base64.getUrlEncoder().encodeToString(potpisanToken.getBytes(StandardCharsets.UTF_8));

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
             LOGGER.info("Email send to user: " + user.getEmail() + ", for passwordless login");
         }catch (MessagingException e){
             LOGGER.error("Failed to send email to user: "+ user.getEmail(), e);
             throw new IllegalStateException("Failed to send email");
         }
        }

        public User confirmLogin(String token, String email) throws NoSuchAlgorithmException, InvalidKeyException {
            String decodedToken = decodeRequestToken(token);
            User user = checkIsLoginTokenValid(decodedToken, email);
            return user;
        }

        private String decodeRequestToken(String token){
            byte[] decodedBytes = Base64.getUrlDecoder().decode(token);
            String decodedToken = new String(decodedBytes, StandardCharsets.UTF_8);
            return decodedToken;
        }

        @Async("threadPoolTaskExecutor")
        public void sendActivationEmail(User user) throws NoSuchAlgorithmException, InvalidKeyException {

            String token = UUID.randomUUID().toString();
            EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(10),
                    user.getId(),
                    "ACTIVATE_ACCOUNT"
            );
            emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);

            String signedToken = hmacUtil.signToken(token);
            String encodedToken = Base64.getUrlEncoder().encodeToString(signedToken.getBytes(StandardCharsets.UTF_8));

            String link = "http://localhost:8040/api/auth/activate?token=" + encodedToken +"&email="+user.getEmail();
            try {
                MimeMessage mimeMessage = emailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
                String htmlContent = "<h1>Please verify link</h1>"+" <p>Hello, click on the link to activate your <a href=\""+link+"\"> account <a></p>";
                messageHelper.setText(htmlContent, true);
                System.out.println(user.getEmail());
                messageHelper.setTo(user.getEmail());
                messageHelper.setSubject("User registration request answer:");
                messageHelper.setFrom("praksaproba1@gmail.com");
                emailSender.send(mimeMessage);

            }catch (MessagingException e){
                LOGGER.error("Failed to send activation email to  " + user.getEmail(), e);
                throw new IllegalStateException("Failed to send email");
            }

        }

    @Async("threadPoolTaskExecutor")
    public void sendEmail(User user, String msg) throws NoSuchAlgorithmException, InvalidKeyException {
        user.setStatus(Status.INACTIVE);
        user.setDeclineDateTime(LocalDateTime.now());
        userRepository.save(user);
        String token = UUID.randomUUID().toString();
        EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                user.getId(),
                "DECLINE_REQUEST"
        );
        emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);

        String signedToken = hmacUtil.signToken(token);
        String encodedToken = Base64.getUrlEncoder().encodeToString(signedToken.getBytes(StandardCharsets.UTF_8));

        String link = "http://localhost:8040/api/auth/activate?token=" + encodedToken +"&email="+user.getEmail();
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            String htmlContent = msg;
            messageHelper.setText(htmlContent, true);
            System.out.println(user.getEmail());
            messageHelper.setTo(user.getEmail());
            messageHelper.setSubject("User registration request answer:");
            messageHelper.setFrom("praksaproba1@gmail.com");
            emailSender.send(mimeMessage);

        }catch (MessagingException e){
            LOGGER.error("Failed to send email", e);
            throw new IllegalStateException("Failed to send email");
        }

    }

        public void activateAccount(String token, String email) throws NoSuchAlgorithmException, InvalidKeyException {
            String decodedToken = decodeRequestToken(token);
            User user = userRepository.findOneByEmail(email);
            if(checkIsTokenValid(decodedToken, user)){

                user.setActivated(true);
                System.out.println(user.getRole().getId());
                if(user.getRole().getId() == 2){
                    System.out.println(LocalDate.now());
                    user.setStartOfEmployment(LocalDate.now());
                }
                user.setStatus(Status.ACTIVE);
                userRepository.save(user);
            }


        }

        private boolean checkIsTokenValid(String decodedToken, User user) throws NoSuchAlgorithmException, InvalidKeyException {
            if(user != null){
                EmailConfirmationToken emailConfirmationToken = emailConfirmationTokenService.findByUserIdAndType(user.getId(), "ACTIVATE_ACCOUNT");
                if(hmacUtil.verifySignature(emailConfirmationToken.getToken(), decodedToken)){
                    LOGGER.info("Token is signature is valid");
                    if(emailConfirmationToken.getExpiredAt().isAfter(LocalDateTime.now())){
                        LOGGER.info("Token is not expired");
                        return true;
                    }
                    LOGGER.warn("Token expired");
                    return false;
                }
                LOGGER.warn("Token signature is not valid");
                return false;
            }
            LOGGER.warn("User does not exist");
            return false;
        }


        private User checkIsLoginTokenValid(String decodedToken,String email) throws NoSuchAlgorithmException, InvalidKeyException {
            User user = userRepository.findOneByEmail(email);
            if(user != null){
                List<EmailConfirmationToken> emailConfirmationTokens
                        = emailConfirmationTokenService.findAllByUserIdAndTypeAndLinkStatus(user.getId(),
                                                                                                 "CONFIRM_LOGIN",
                                                                                                    "PENDING");
                if(emailConfirmationTokens.size() == 1){
                    if(isTokenValid(emailConfirmationTokens.get(0), decodedToken)){
                        emailConfirmationTokens.get(0).setConfirmedAt(LocalDateTime.now());
                        emailConfirmationTokens.get(0).setLinkStatus("VERIFIED");
                        emailConfirmationTokenService.saveConfirmationToken(emailConfirmationTokens.get(0));
                        return user;
                    }else{
                        return null;
                    }
                } else if (emailConfirmationTokens.size() > 1) {
                        EmailConfirmationToken emailConfirmationToken = checkAllUserTokens(user);
                        if(isNewestTokenValid(emailConfirmationToken, decodedToken)) {
                           emailConfirmationToken.setConfirmedAt(LocalDateTime.now());
                           emailConfirmationToken.setLinkStatus("VERIFIED");
                           emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);
                           return user;
                       }else{
                            return null;
                        }
                }else {
                    LOGGER.warn("User " + email + " failed to login with link; REASON: Link is expired");
                    return null;
                }

            }
         return null;
        }

        private EmailConfirmationToken checkAllUserTokens(User user){
            List<EmailConfirmationToken> emailConfirmationTokens
                    = emailConfirmationTokenService.findAllByUserIdAndTypeAndLinkStatus(user.getId(),
                    "CONFIRM_LOGIN",
                    "PENDING");
            EmailConfirmationToken newestToken = emailConfirmationTokens.stream()
                    .max(Comparator.comparing(EmailConfirmationToken::getIssuedAt))
                    .orElse(null);
            emailConfirmationTokens.remove(newestToken);

            for (EmailConfirmationToken emailToken: emailConfirmationTokens) {
                emailToken.setLinkStatus("EXPIRED");
                emailConfirmationTokenService.saveConfirmationToken(emailToken);
            }

         return newestToken;
        }

        private boolean isTokenValid(EmailConfirmationToken emailConfirmationToken, String decodedToken) throws NoSuchAlgorithmException, InvalidKeyException {
            if(hmacUtil.verifySignature(emailConfirmationToken.getToken(), decodedToken)){
                if(emailConfirmationToken.getExpiredAt().isAfter(LocalDateTime.now())){
                    LOGGER.info("Login Token is not expired");
                    return true;
                }
                emailConfirmationToken.setLinkStatus("EXPIRED");
                emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);
                LOGGER.warn("Login Token is expired");
                return false;
            }
            emailConfirmationToken.setLinkStatus("EXPIRED");
            emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);
            LOGGER.warn("Login Token signature is not valid");
            return false;
        }

    private boolean isNewestTokenValid(EmailConfirmationToken emailConfirmationToken, String decodedToken) throws NoSuchAlgorithmException, InvalidKeyException {
        if(hmacUtil.verifySignature(emailConfirmationToken.getToken(), decodedToken)){
            LOGGER.info("Login Token signature is valid");
            if(emailConfirmationToken.getExpiredAt().isAfter(LocalDateTime.now())){
                LOGGER.info("Login Token is not expired");
                return true;
            }
            emailConfirmationToken.setLinkStatus("EXPIRED");
            emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);
            LOGGER.warn("Login Token is expired");
            return false;
        }
        LOGGER.warn("Login Token signature is not valid");
        return false;
    }

}
