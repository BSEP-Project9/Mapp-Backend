package com.example.Mapp.service;

import com.example.Mapp.DTO.UserDTO;
import com.example.Mapp.mapper.UserMapper;
import com.example.Mapp.model.Address;
import com.example.Mapp.model.Role;
import com.example.Mapp.model.User;
import com.example.Mapp.repository.RoleRepository;
import com.example.Mapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final AddressService addressService;

    public UserService(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository, AddressService addressService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.addressService = addressService;
    }

    public User register(UserDTO userDTO){
            Optional<User> user = userRepository.findByEmailAndPassword(userDTO.getEmail(), userDTO.getPassword());
            if(!user.isPresent()){
                User credentials = create(userDTO);
                userRepository.save(credentials);
                System.out.println("EvoME I OVde");
                return credentials;
            } else return null;

    }

    private User create(UserDTO userDTO){
        User credentials = userMapper.DtoToEntity(userDTO);
        Role role = roleRepository.findByName(userDTO.getRole());
        Address address = addressService.create(userDTO.getAddress());
        credentials.setRole(role);
        credentials.setAddress(address);
        return credentials;
    }

    public List<UserDTO> getAllInactiveUsers(){
        List<User> users = userRepository.findAll();
        List<User> usersCopy = new ArrayList<>();
        users.forEach(user -> {
            if(!user.isActivated()){
                usersCopy.add(user);
            }
        });
        List<UserDTO> usersFinal = new ArrayList<>();
        usersCopy.forEach(user -> {
            usersFinal.add(userMapper.EntityToDto(user));
        });
        return usersFinal;
    }

}
