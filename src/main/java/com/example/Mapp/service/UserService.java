package com.example.Mapp.service;

import java.util.List;

import com.example.Mapp.dto.AddressDTO;
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
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

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

    public User register(UserDTO userDTO){
        Optional<User> user = userRepository.findByEmailAndPassword(userDTO.getEmail(), userDTO.getPassword());
        if(!user.isPresent()){
            User credentials = create(userDTO);
            userRepository.save(credentials);
            return credentials;
        }
        return null;
    }

    private User create(UserDTO userDTO){
        User credentials = userMapper.DtoToEntity(userDTO);
        Role role = roleRepository.findByName(userDTO.getRole());
        Address address = addressService.create(userDTO.getAddress());
        credentials.setRole(role);
        credentials.setAddress(address);
        return credentials;
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
