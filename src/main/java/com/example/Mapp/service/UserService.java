package com.example.Mapp.service;

import com.example.Mapp.dto.UserDTO;
import com.example.Mapp.exceptions.RegistrationException;

import java.util.List;

import com.example.Mapp.dto.LoggedUserDTO;
import com.example.Mapp.dto.UserDTO;
import com.example.Mapp.mapper.UserMapper;
import com.example.Mapp.model.Address;
import com.example.Mapp.model.Role;
import com.example.Mapp.model.User;
import com.example.Mapp.repository.RoleRepository;
import com.example.Mapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final AddressService addressService;
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);


    public UserService(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository, AddressService addressService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.addressService = addressService;
    }


    public User register(UserDTO userDTO) {
        try {
            if (!userDTO.getConfirmPassword().equals(userDTO.getPassword())) {
                throw new RegistrationException("Passwords Do Not Match!");
            }
        } catch (RegistrationException e) {
            throw e;
        }

        try {
            if (!isPasswordValid(userDTO.getPassword())) {
                throw new RegistrationException("Password does not meet the requirements!");
            }
        } catch (RegistrationException e) {
            throw e;
        }
        Optional<User> user = userRepository.findByEmailAndPassword(userDTO.getEmail(), userDTO.getPassword());
        if (!user.isPresent()) {
            User credentials = create(userDTO);
            userRepository.save(credentials);
            return credentials;
        } else return null;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User edit(User user, Long id){
        Optional<User> OldCenter = userRepository.findById(id);
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


    private User create(UserDTO userDTO) {
        User credentials = userMapper.DtoToEntity(userDTO);
        Role role = roleRepository.findByName(userDTO.getRole());
        Address address = addressService.create(userDTO.getAddress());
        credentials.setRole(role);
        credentials.setAddress(address);
        return credentials;
    }

    public List<UserDTO> getAllInactiveUsers() {
        List<User> users = userRepository.findAll();
        List<User> usersCopy = new ArrayList<>();
        users.forEach(user -> {
            if (!user.isActivated()) {
                usersCopy.add(user);
            }
        });
        List<UserDTO> usersFinal = new ArrayList<>();
        usersCopy.forEach(user -> {
            usersFinal.add(userMapper.EntityToDto(user));
        });
        return usersFinal;
    }

    public boolean isPasswordValid(String password) {
        return pattern.matcher(password).matches();
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findOneByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", username));
        }else {
            return user;
        }
    }

    public LoggedUserDTO getByEmail(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            LoggedUserDTO dto = new LoggedUserDTO();
            dto.setId(user.get().getId().toString());
            dto.setRole(user.get().getRole().getName());
            return dto;
        }
        return null;
    }
}
