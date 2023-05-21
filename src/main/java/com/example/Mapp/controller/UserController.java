package com.example.Mapp.controller;

import com.example.Mapp.DTO.LoginDTO;
import com.example.Mapp.DTO.UserDTO;
import com.example.Mapp.DTO.UserTokenStateDTO;
import com.example.Mapp.config.JwtService;
import com.example.Mapp.model.User;
import com.example.Mapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    @PostMapping("/register")
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
        User newUser = (User) auth.getPrincipal();
        var jwtToken = jwtService.generateToken(newUser);
        int expiresIn = jwtService.getExpiredIn();

        return new UserTokenStateDTO(jwtToken, jwtToken,(long) expiresIn, newUser.getRole().getName(), newUser.getId().toString());
    }
}
