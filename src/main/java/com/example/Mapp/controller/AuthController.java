package com.example.Mapp.controller;

import com.example.Mapp.config.JwtService;
import com.example.Mapp.confirmation.HmacUtil;
import com.example.Mapp.dto.EmailLoginDTO;
import com.example.Mapp.dto.LoginDTO;
import com.example.Mapp.dto.UserDTO;
import com.example.Mapp.dto.UserTokenStateDTO;
import com.example.Mapp.model.User;
import com.example.Mapp.service.EmailService;
import com.example.Mapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private final EmailService emailService;
    private final HmacUtil hmacUtil;

    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager, UserService userService, EmailService emailService, HmacUtil hmacUtil) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.emailService = emailService;
        this.hmacUtil = hmacUtil;
    }

    @PostMapping
    public ResponseEntity register(@RequestBody UserDTO userDTO){
        User user = userService.register(userDTO);
        if(user == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public UserTokenStateDTO authentication(@RequestBody LoginDTO loginDTO){
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );
        User loggedUser = (User) auth.getPrincipal();
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role",loggedUser.getRole().getName());
        var jwtToken = jwtService.generateToken(extraClaims, loggedUser);
        System.out.println(jwtToken);

        //TO DO: generate refresh token and send it to the client

        return new UserTokenStateDTO(jwtToken, jwtToken);
    }

    @PostMapping("/email-login")
    public UserTokenStateDTO emailAuthentication(@RequestBody EmailLoginDTO email) throws NoSuchAlgorithmException, InvalidKeyException {
         User user = userService.getUserByEmail(email);
          if(user != null){
            emailService.sendActivationEmail(user);
        }
        return null;
    }

    @GetMapping("/check-email/confirm")
    public ResponseEntity<Object> checkEmailVerificationToken(@RequestParam String token, @RequestParam String email) throws NoSuchAlgorithmException, InvalidKeyException {
        if(!token.isEmpty() && !email.isEmpty()){
            User user = emailService.confirmLogin(token, email);
                if(user != null){
                    System.out.println("HAIIIII");
                    System.out.println("Ovo je iz zaglavlja: " + token);
                    Map<String, Object> extraClaims = new HashMap<>();
                    extraClaims.put("role",user.getRole().getName());
                    var jwtToken = jwtService.generateToken(extraClaims, user);
                    System.out.println(jwtToken);

            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/proba")
    public ResponseEntity proba() throws NoSuchAlgorithmException, InvalidKeyException {
        String token = UUID.randomUUID().toString();
        System.out.println("Random vrednost: "+ token);
        String secretKey = "tajni_kljuc";
        String potpisanToken = hmacUtil.signToken(token, secretKey);
        System.out.println("Potpisan Hmacpm: "+ potpisanToken);
        String encodedToken = Base64.getUrlEncoder().encodeToString(potpisanToken.getBytes(StandardCharsets.UTF_8));
        System.out.println("Poslato u zaglavlju: "+ encodedToken);
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedToken);
        String decodedToken = new String(decodedBytes, StandardCharsets.UTF_8);
        System.out.println("Trebalo bi da bude original:  " +decodedToken);
        return null;
    }
}
