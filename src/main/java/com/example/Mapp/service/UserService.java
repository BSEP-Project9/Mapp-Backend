package com.example.Mapp.service;

import com.example.Mapp.dto.ReturningUserDTO;
import com.example.Mapp.dto.UserDTO;
import com.example.Mapp.exceptions.RegistrationException;

import java.util.List;
import com.example.Mapp.dto.EmailLoginDTO;
import com.example.Mapp.dto.LoggedUserDTO;
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
import java.util.ArrayList;

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

    public User edit(UserDTO user){
        Optional<User> oldUserOptional = userRepository.findByEmail(user.getEmail());
        if(oldUserOptional.isEmpty()) {
            return null;
        }
        User oldUser = oldUserOptional.get();
        Address newAddress = addressService.getById(oldUser.getAddress().getId());
        newAddress.setCity(user.getAddress().getCity());
        oldUser.setAddress(newAddress);
        oldUser.setName(user.getName());
        oldUser.setPhoneNumber(user.getPhoneNumber());
        oldUser.setSurname(user.getSurname());
        oldUser.setEmail(user.getEmail());
        return userRepository.save(oldUser);
    }
    public UserDTO getById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {
            return null;
        }
        UserDTO userDTO = userMapper.EntityToDto(user.get());
        return userDTO;
    }


    private User create(UserDTO userDTO) {
        User credentials = userMapper.DtoToEntity(userDTO);
        Role role = roleRepository.findByName(userDTO.getRole());
        Address address = addressService.create(userDTO.getAddress());
        credentials.setRole(role);
        credentials.setAddress(address);
        return credentials;
    }

    public List<ReturningUserDTO> getAllInactiveUsers() {
        List<User> users = userRepository.findAll();
        List<User> usersCopy = new ArrayList<>();
        users.forEach(user -> {
            if (!user.isActivated()) {
                usersCopy.add(user);
            }
        });
        List<ReturningUserDTO> usersFinal = new ArrayList<>();
        usersCopy.forEach(user -> {
            usersFinal.add(userMapper.EntityToReturningDTO(user));
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

    public User getUserByEmail(EmailLoginDTO email){
        Optional<User> user = userRepository.findByEmail(email.getEmail());
        if(user.isPresent()){
            return user.get();
        }
        return null;
    }

    public User getOneByEmail(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            return user.get();
        }
        return null;
    }
}
