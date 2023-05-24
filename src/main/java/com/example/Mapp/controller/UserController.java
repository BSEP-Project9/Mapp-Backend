package com.example.Mapp.controller;

import com.example.Mapp.DTO.UserDTO;
import com.example.Mapp.model.User;
import com.example.Mapp.service.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO){
        System.out.println(userDTO);
        System.out.println("TU SAM SAD!");
        User user = userService.register(userDTO);
        if(user == null){
            return ResponseEntity.badRequest().body("Error within registration");
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity getAllInactiveUsers(){
        List<UserDTO> users = userService.getAllInactiveUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


}
