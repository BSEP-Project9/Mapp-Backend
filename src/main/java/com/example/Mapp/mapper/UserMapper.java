package com.example.Mapp.mapper;
import com.example.Mapp.dto.ReturningUserDTO;
import com.example.Mapp.dto.AddressDTO;
import com.example.Mapp.dto.UserDTO;
import com.example.Mapp.model.Role;
import com.example.Mapp.model.User;
import com.example.Mapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private AddressMapper addressMapper;



    public UserMapper(RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
        dto.setStatus(model.getStatus());
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
        System.out.println(role.getName());
        if (role != null) {
            System.out.println(role);
            model.setRole(role);
        }
        model.setStatus(dto.getStatus());
        return model;
    }
    public ReturningUserDTO EntityToReturningDTO(User model) {
        ReturningUserDTO dto = new ReturningUserDTO();
        dto.setId(model.getId());
        dto.setEmail(model.getEmail());
        dto.setName(model.getName());
        dto.setSurname(model.getSurname());
        dto.setActivated(model.isActivated());
        return dto;
    }
}
