package com.example.Mapp.controller;

import com.example.Mapp.config.JwtService;
import com.example.Mapp.dto.LoginDTO;
import com.example.Mapp.dto.UserDTO;
import com.example.Mapp.dto.UserTokenStateDTO;
import com.example.Mapp.model.User;
import com.example.Mapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
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
        String role = userService.getByEmail(loginDTO.getEmail()).getRole();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", loggedUser.getRole().getAuthority());

        var accessToken = jwtService.generateToken(extraClaims, loggedUser);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), loggedUser);

        //TO DO: generate refresh token and send it to the client

        return new UserTokenStateDTO(accessToken, refreshToken);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String userEmail;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if(userEmail != null){
            UserDetails userDetails = this.userService.loadUserByUsername(userEmail);
            if(jwtService.isTokenValid(refreshToken, userDetails)){

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                User loggedUser = (User) userDetails;
                String role = userService.getByEmail(loggedUser.getEmail()).getRole();

                Map<String, Object> extraClaims = new HashMap<>();
                extraClaims.put("role", loggedUser.getRole().getAuthority());

                String accessToken = jwtService.generateToken(extraClaims, loggedUser);
                new ObjectMapper().writeValue(response.getOutputStream(), new UserTokenStateDTO(accessToken, refreshToken));
            }
        }
    }

}
