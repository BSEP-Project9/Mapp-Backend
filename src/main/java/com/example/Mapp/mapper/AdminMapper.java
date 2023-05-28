package com.example.Mapp.mapper;

import com.example.Mapp.dto.AddressDTO;
import com.example.Mapp.dto.AdminDTO;
import com.example.Mapp.model.Role;
import com.example.Mapp.model.User;
import com.example.Mapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AddressMapper addressMapper;

    public AdminDTO EntityToDto(User model){
        AdminDTO dto = new AdminDTO();
        dto.setEmail(model.getEmail());
        dto.setPassword(model.getPassword());
        dto.setName(model.getName());
        dto.setSurname(model.getSurname());
        dto.setPhoneNumber(model.getPhoneNumber());
        AddressDTO addressDTO = addressMapper.EntityToDto(model.getAddress());
        dto.setAddress(addressDTO);
        return dto;
    }

    public User DtoToEntity(AdminDTO dto) {
        User model = new User();
        model.setEmail(dto.getEmail());
        model.setPassword(dto.getPassword());
        model.setName(dto.getName());
        model.setSurname(dto.getSurname());
        model.setPhoneNumber(dto.getPhoneNumber());
        Role role = roleRepository.findByName("ROLE_ADMIN");

        if (role != null) {
            System.out.println(role);
            model.setRole(role);
        }

        return model;
    }
}
