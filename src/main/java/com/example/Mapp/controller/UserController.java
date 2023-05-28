package com.example.Mapp.controller;

import com.example.Mapp.config.JwtService;
import com.example.Mapp.dto.LoggedUserDTO;

import com.example.Mapp.dto.AdminDTO;
import com.example.Mapp.dto.ReturningUserDTO;
import com.example.Mapp.dto.UserDTO;
import com.example.Mapp.model.Skill;
import com.example.Mapp.model.User;
import com.example.Mapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        System.out.println(userDTO);
        System.out.println("TU SAM SAD!");
        User user = userService.register(userDTO);
        if (user == null) {
            return ResponseEntity.badRequest().body("Error within registration");
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping()
    public User edit(@RequestBody UserDTO user) {
        return userService.edit(user);
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable("id") Long id) {
        return userService.getById(id);
    }

    @GetMapping("/email/{email}")
    public LoggedUserDTO getByEmail(@PathVariable("email") String email){
        LoggedUserDTO loggedUserDTO = userService.getByEmail(email);
        return loggedUserDTO;
    }

    @GetMapping
    public ResponseEntity getAllInactiveUsers(){
        List<ReturningUserDTO> users = userService.getAllInactiveUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/all")
    public List<User> getAll(){
       return userService.getAll();
    }

    @PostMapping("/register-admin")
    public User registerAdmin(@RequestBody AdminDTO adminDTO){
        System.out.println("**************** " + adminDTO.getAddress());
        return  userService.registerAdmin(adminDTO);
    }

    @PostMapping("/{userId}/add-skill")
    public void addSkill(@RequestBody Skill skill, @PathVariable("userId") Long userId){
        userService.addSkill(skill, userId);
    }


}
