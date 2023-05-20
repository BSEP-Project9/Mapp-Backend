package com.example.Mapp.service;

import com.example.Mapp.model.User;
import com.example.Mapp.repository.UserRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User edit(User user, Long id){
        Optional<User> OldCenter = userRepository.findById(id);     //provjeravam da li postoji u bazi
        if(OldCenter.isEmpty()) {
            return null;
        }
        return userRepository.save(user);
    }
    public User getById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {
            return null;
        }
        return user.get();
    }


//    public List<User> getAllByProjectAndRole(Long id, String role) {
//        return userRepository.findAllBy
//    }


}
