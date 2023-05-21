package com.example.Mapp.mapper;

import com.example.Mapp.DTO.UserDTO;
import com.example.Mapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

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
        model.setPassword(dto.getPassword());
        model.setName(dto.getName());
        model.setSurname(dto.getSurname());
        model.setPhoneNumber(dto.getPhoneNumber());
        return model;
    }
}
