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
         System.out.println("OVO JE RANDOM UUID: " + token);
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
         System.out.println("Hmac token: " + potpisanToken);
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

            String token = UUID.randomUUID().toString(); //Random generisana vrednost tokena
            EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
                    token,
                    LocalDateTime.now(), //datum kreiranja tokena
                    LocalDateTime.now().plusMinutes(10), //datum isteka vazenja tokena {DODATI DRUGU VREDNOST ZA TRAJANJE}
                    user.getId(), // id korisnika kome saljemo email, da bi mogli proveriti da li je na vreme usao na link,
                    "ACTIVATE_ACCOUNT"
            );
            emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);//cuvamo u bazi

            String signedToken = hmacUtil.signToken(token); //Primenjujemo HMAC algoritam na nas token
            String encodedToken = Base64.getUrlEncoder().encodeToString(signedToken.getBytes(StandardCharsets.UTF_8));
            //Kad se primeni HMAC algoritam, token moze da sadrzi specijalne znake
            //kao sto su '/' i to moze da napravi problem pri citanju putanja iz zaglavlja, npr ..api/v1/67yugu/jg7tyty, ovde je 67yugu/jg7tyty nas token
            //da bi izbegli ovakve situacije moramo ga jos jednom enkodovati ovim cudom gore da bi se neutralisali ti zbunjujuci znaci

            String link = "http://localhost:8040/api/auth/activate?token=" + encodedToken +"&email="+user.getEmail();
            //definisemo link u emailu, to ce biti putanja na beku koju zelimo da pogodimo kad korisnik klikne na link iz mejla
            //prosledjujemo token radi vremenske provere
            //prosledjujemo mejl da znamo o kom se coveku radi

            //pisanje mejla
            try {
                MimeMessage mimeMessage = emailSender.createMimeMessage(); //ne diraj
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8"); //ne diraj
                String htmlContent = "<h1>Please verify link</h1>"+" <p>Hello, click on the link to activate your <a href=\""+link+"\"> account <a></p>"; //izmeni poruku
                messageHelper.setText(htmlContent, true); //ne diraj
                System.out.println(user.getEmail());
                messageHelper.setTo(user.getEmail()); //ne diraj, ili mozes ako hoces da eksperimentises sa ovim
                messageHelper.setSubject("User registration request answer:"); // naslov, promeni
                messageHelper.setFrom("praksaproba1@gmail.com"); //ne diraj, ili mozes ako hoces da eksperimentises sa ovim
                emailSender.send(mimeMessage);

            }catch (MessagingException e){
               // LOGGER.error("Failed to send email", e);
                throw new IllegalStateException("Failed to send email");
            }

        }

    @Async("threadPoolTaskExecutor")
    public void sendEmail(User user, String msg) throws NoSuchAlgorithmException, InvalidKeyException {
        user.setStatus(Status.INACTIVE);
        user.setDeclineDateTime(LocalDateTime.now());
        userRepository.save(user);
        String token = UUID.randomUUID().toString(); //Random generisana vrednost tokena
        EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
                token,
                LocalDateTime.now(), //datum kreiranja tokena
                LocalDateTime.now().plusMinutes(10), //datum isteka vazenja tokena {DODATI DRUGU VREDNOST ZA TRAJANJE}
                user.getId(), // id korisnika kome saljemo email, da bi mogli proveriti da li je na vreme usao na link,
                "DECLINE_REQUEST"
        );
        emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);//cuvamo u bazi

        String signedToken = hmacUtil.signToken(token); //Primenjujemo HMAC algoritam na nas token
        String encodedToken = Base64.getUrlEncoder().encodeToString(signedToken.getBytes(StandardCharsets.UTF_8));
        //Kad se primeni HMAC algoritam, token moze da sadrzi specijalne znake
        //kao sto su '/' i to moze da napravi problem pri citanju putanja iz zaglavlja, npr ..api/v1/67yugu/jg7tyty, ovde je 67yugu/jg7tyty nas token
        //da bi izbegli ovakve situacije moramo ga jos jednom enkodovati ovim cudom gore da bi se neutralisali ti zbunjujuci znaci

        String link = "http://localhost:8040/api/auth/activate?token=" + encodedToken +"&email="+user.getEmail();
        //definisemo link u emailu, to ce biti putanja na beku koju zelimo da pogodimo kad korisnik klikne na link iz mejla
        //prosledjujemo token radi vremenske provere
        //prosledjujemo mejl da znamo o kom se coveku radi

        //pisanje mejla
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage(); //ne diraj
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8"); //ne diraj
            String htmlContent = msg; //izmeni poruku
            messageHelper.setText(htmlContent, true); //ne diraj
            System.out.println(user.getEmail());
            messageHelper.setTo(user.getEmail()); //ne diraj, ili mozes ako hoces da eksperimentises sa ovim
            messageHelper.setSubject("User registration request answer:"); // naslov, promeni
            messageHelper.setFrom("praksaproba1@gmail.com"); //ne diraj, ili mozes ako hoces da eksperimentises sa ovim
            emailSender.send(mimeMessage);

        }catch (MessagingException e){
            //LOGGER.error("Failed to send email", e);
            throw new IllegalStateException("Failed to send email");
        }

    }

        public void activateAccount(String token, String email) throws NoSuchAlgorithmException, InvalidKeyException {
            String decodedToken = decodeRequestToken(token);
            User user = userRepository.findOneByEmail(email);
            if(checkIsTokenValid(decodedToken, user)){
                //....
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
            if(user != null){ //proveravamo da li uopste postoji korisnik s datim email-om
                EmailConfirmationToken emailConfirmationToken = emailConfirmationTokenService.findByUserIdAndType(user.getId(), "ACTIVATE_ACCOUNT");
                if(hmacUtil.verifySignature(emailConfirmationToken.getToken(), decodedToken)){
                    System.out.println("Token je ispravano potpisan");
                    if(emailConfirmationToken.getExpiredAt().isAfter(LocalDateTime.now())){
                        System.out.println("Token je vremenski ispravan");
                        return true;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }

        ///************************************************************************************************************
        private User checkIsLoginTokenValid(String decodedToken,String email) throws NoSuchAlgorithmException, InvalidKeyException {
            User user = userRepository.findOneByEmail(email);
            if(user != null){
                List<EmailConfirmationToken> emailConfirmationTokens
                        = emailConfirmationTokenService.findAllByUserIdAndTypeAndLinkStatus(user.getId(),
                                                                                                 "CONFIRM_LOGIN",
                                                                                                    "PENDING");
                if(emailConfirmationTokens.size() == 1){ //#1
                    System.out.println("Lista ima samo jedan clan");
                    if(isTokenValid(emailConfirmationTokens.get(0), decodedToken)){
                        emailConfirmationTokens.get(0).setConfirmedAt(LocalDateTime.now());
                        emailConfirmationTokens.get(0).setLinkStatus("VERIFIED");
                        emailConfirmationTokenService.saveConfirmationToken(emailConfirmationTokens.get(0));
                        return user;
                    }else{
                        return null;
                    }
                } else if (emailConfirmationTokens.size() > 1) {//#2
                    System.out.println("Lista ima vise clanova");
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
                    System.out.println("Token je vec iskoriscen ili je istekao");
                    return null;
                }

            }
         return user;
        }

        private EmailConfirmationToken checkAllUserTokens(User user){
            List<EmailConfirmationToken> emailConfirmationTokens
                    = emailConfirmationTokenService.findAllByUserIdAndTypeAndLinkStatus(user.getId(),
                    "CONFIRM_LOGIN",
                    "PENDING");
            EmailConfirmationToken newestToken = emailConfirmationTokens.stream()
                    .max(Comparator.comparing(EmailConfirmationToken::getIssuedAt))
                    .orElse(null);
            System.out.println("Ovo je najnoviji token: " + newestToken);
            emailConfirmationTokens.remove(newestToken);

            for (EmailConfirmationToken emailToken: emailConfirmationTokens) {
                emailToken.setLinkStatus("EXPIRED");
                emailConfirmationTokenService.saveConfirmationToken(emailToken);
            }

         return newestToken;
        }

        private boolean isTokenValid(EmailConfirmationToken emailConfirmationToken, String decodedToken) throws NoSuchAlgorithmException, InvalidKeyException {
            if(hmacUtil.verifySignature(emailConfirmationToken.getToken(), decodedToken)){
                System.out.println("Token je ispravano potpisan");
                System.out.println("Datum isteka tokena: " + emailConfirmationToken.getExpiredAt());
                System.out.println("Vreme provere: " + LocalDateTime.now());
                if(emailConfirmationToken.getExpiredAt().isAfter(LocalDateTime.now())){
                    System.out.println("Token je vremenski ispravan");
                    return true;
                }
                emailConfirmationToken.setLinkStatus("EXPIRED");
                emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);
                System.out.println("Token je istekao");
                return false;
            }
            emailConfirmationToken.setLinkStatus("EXPIRED");
            emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);
            System.out.println("Token je lose potpisan");
            return false;
        }

    private boolean isNewestTokenValid(EmailConfirmationToken emailConfirmationToken, String decodedToken) throws NoSuchAlgorithmException, InvalidKeyException {
        if(hmacUtil.verifySignature(emailConfirmationToken.getToken(), decodedToken)){
            System.out.println("Token je ispravano potpisan");
            System.out.println("Datum isteka tokena: " + emailConfirmationToken.getExpiredAt());
            System.out.println("Vreme provere: " + LocalDateTime.now());
            if(emailConfirmationToken.getExpiredAt().isAfter(LocalDateTime.now())){
                System.out.println("Token je vremenski ispravan");
                return true;
            }
            emailConfirmationToken.setLinkStatus("EXPIRED");
            emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);
            System.out.println("Token je istekao");
            return false;
        }
        //emailConfirmationToken.setLinkStatus("EXPIRED");
        //emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);
        System.out.println("Token je lose potpisan");
        return false;
    }

}
