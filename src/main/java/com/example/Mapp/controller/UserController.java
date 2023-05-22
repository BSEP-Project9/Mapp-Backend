package com.example.Mapp.controller;


import com.example.Mapp.dto.LoggedUserDTO;
import com.example.Mapp.config.JwtService;
import com.example.Mapp.model.User;
import com.example.Mapp.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @PutMapping("/{id}")
    public User edit(@RequestBody User user, @PathVariable("id") Long id) {
        return userService.edit(user, id);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Long id) {
        return userService.getById(id);
    }

    @GetMapping("/email/{email}")
    public LoggedUserDTO getByEmail(@PathVariable("email") String email){
        LoggedUserDTO loggedUserDTO = userService.getByEmail(email);
        return loggedUserDTO;
    }
}
