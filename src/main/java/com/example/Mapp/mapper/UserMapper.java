package com.example.Mapp.mapper;

import com.example.Mapp.dto.AddressDTO;
import com.example.Mapp.dto.UserDTO;
import com.example.Mapp.enums.Seniority;
import com.example.Mapp.model.Role;
import com.example.Mapp.model.User;
import com.example.Mapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.Mapp.dto.UserDTO;
import com.example.Mapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserMapper {

    @Autowired

    private RoleRepository roleRepository;
    @Autowired
    private AddressMapper addressMapper;

    private PasswordEncoder passwordEncoder;


    public UserDTO EntityToDto(User model){
        UserDTO dto = new UserDTO();
        dto.setEmail(model.getEmail());
        dto.setPassword(model.getPassword());
        dto.setName(model.getName());
        dto.setSurname(model.getSurname());
        dto.setPhoneNumber(model.getPhoneNumber());
        AddressDTO addressDTO = addressMapper.EntityToDto(model.getAddress());
        dto.setAddress(addressDTO);
        dto.setRole(model.getRole().getName());
        return dto;
    }

    public User DtoToEntity(UserDTO dto) {
        User model = new User();
        model.setEmail(dto.getEmail());
        model.setPassword(passwordEncoder.encode(dto.getPassword()));
        model.setName(dto.getName());
        model.setSurname(dto.getSurname());
        model.setPhoneNumber(dto.getPhoneNumber());
        System.out.println(dto.getRole());
        Role role = roleRepository.findByName(dto.getRole());

        if (role != null) {
            System.out.println(role);
            model.setRole(role);
        }

        return model;
    }
}
