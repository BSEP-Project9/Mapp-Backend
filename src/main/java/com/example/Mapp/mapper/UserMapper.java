package com.example.Mapp.mapper;

import com.example.Mapp.dto.UserDTO;
import com.example.Mapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO EntityToDto(User model){
        UserDTO dto = new UserDTO();
        dto.setEmail(model.getEmail());
        dto.setPassword(model.getPassword());
        dto.setName(model.getName());
        dto.setSurname(model.getSurname());
        dto.setPhoneNumber(model.getPhoneNumber());
        return dto;
    }

    public User DtoToEntity(UserDTO dto){
        User model = new User();
        model.setEmail(dto.getEmail());
        model.setPassword(passwordEncoder.encode(dto.getPassword()));
        model.setName(dto.getName());
        model.setSurname(dto.getSurname());
        model.setPhoneNumber(dto.getPhoneNumber());
        return model;
    }
}
