package com.example.Mapp.controller;

import com.example.Mapp.model.User;
import com.example.Mapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
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

//    @GetMapping("/project/{id}/swe") //na projektu moze biti i pm, zato ovdje kazem swe
//    public List<User> getAllSweByProject(@PathVariable("id") Long id) {
//        return userService.getAllByProjectAndRole(id, "SWE");
//    }
}
