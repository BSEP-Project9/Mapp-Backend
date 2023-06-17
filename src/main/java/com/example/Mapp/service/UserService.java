package com.example.Mapp.service;

import com.example.Mapp.dto.*;
import com.example.Mapp.dto.ReturningUserDTO;
import com.example.Mapp.dto.AdminDTO;
import com.example.Mapp.dto.UserDTO;
import com.example.Mapp.enums.Status;
import com.example.Mapp.exceptions.RegistrationException;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

import com.example.Mapp.mapper.AdminMapper;
import com.example.Mapp.mapper.UserMapper;
import com.example.Mapp.model.*;
import com.example.Mapp.repository.RoleRepository;
import com.example.Mapp.repository.SkillRepository;
import com.example.Mapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final ContributionService contributionService;
    private final ProjectService projectService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
    private final SkillRepository skillRepository;
    private final AdminMapper adminMapper;
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);


    public UserService(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository,
                       AddressService addressService, SkillRepository skillRepository, AdminMapper adminMapper,
                       ContributionService contributionService, ProjectService projectService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.addressService = addressService;
        this.skillRepository = skillRepository;
        this.adminMapper = adminMapper;
        this.contributionService = contributionService;
        this.projectService = projectService;
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
        Optional<User> user = userRepository.findByEmail(userDTO.getEmail());
        if (!user.isPresent()) {
            User credentials = create(userDTO);
            userRepository.save(credentials);
            return credentials;
        } else {
            Duration duration = Duration.between(user.get().getDeclineDateTime(), LocalDateTime.now());
            long daysPassed = duration.toDays();
            long secondsPassed = duration.toSeconds();
            if(user.get().getStatus() == Status.INACTIVE && secondsPassed >= 25){
                userRepository.delete(user.get());
                User credentials = create(userDTO);
                userRepository.save(credentials);
                return credentials;
            }
        }
        return null;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> getAllWorkers() {
        List<Contribution> contributions = contributionService.getAll();
        List<User> workers = extractWorkersFromContributions(contributions);

        return workers;
    }

    private List<User> extractWorkersFromContributions(List<Contribution> contributions) {
        List<User> workers = new ArrayList<>();
        for (Contribution c: contributions) {
            if (c.getWorker().getRole().getAuthority().equals("ROLE_SWE") || c.getWorker().getRole().getAuthority().equals("ROLE_PM")) {
                if (!workers.contains(c.getWorker())) {
                    workers.add(c.getWorker());
                }
            }
        }

        return workers;
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
        credentials.setStatus(Status.PENDING);
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

    public User registerAdmin(AdminDTO adminDTO) {
        User user = adminMapper.DtoToEntity(adminDTO);
        if(adminDTO.getAddress() != null) {
            Address address = addressService.create(adminDTO.getAddress());
            user.setAddress(address);
        }
        return userRepository.save(user);
    }

    public void addSkill(Skill skill, Long userId) {
        User user = userRepository.findById(userId).get();
        user.getSkills().add(skill);
        skill.setUser(user);
        skillRepository.save(skill);
        userRepository.save(user);
    }

    public void editPassword(UpdatePasswordDto updatePasswordDto) {
        Optional<User> oldUserOptional = userRepository.findByEmail(updatePasswordDto.getEmail());
        if(oldUserOptional.isEmpty()) {
            return;
        }
        User oldUser = oldUserOptional.get();
        oldUser.setPassword(passwordEncoder.encode(updatePasswordDto.getUpdatedPassword()));
        userRepository.save(oldUser);
    }

    public void block(String email) {
        Optional<User> oldUserOptional = userRepository.findByEmail(email);
        if(oldUserOptional.isEmpty()) {
            return;
        }
        User oldUser = oldUserOptional.get();
        oldUser.setBlocked(true);
        userRepository.save(oldUser);
    }

    public void unblock(String email) {
        Optional<User> oldUserOptional = userRepository.findByEmail(email);
        if(oldUserOptional.isEmpty()) {
            return;
        }
        User oldUser = oldUserOptional.get();
        oldUser.setBlocked(false);
        userRepository.save(oldUser);
    }

    public List<User> getAllByPm(Long pmId) {
        List<Contribution> pmContributions = contributionService.getAllByWorker(pmId);
        List<Project> pmProjects = extractProjectsFromContributions(pmContributions);
        List<User> pmEmployees = new ArrayList<>();

        for (Project p: pmProjects) {
            List<User> employees = contributionService.getAllWorkerByProject(p.getId());
            if (pmEmployees.isEmpty()) {
                for (User e: employees) {
                    if (!e.getId().equals(pmId)) {
                        pmEmployees.add(e);
                    }
                }
            }

            for (User e: employees) {
                if (e.getId().equals(pmId)) {
                    continue;
                }
                if (!pmEmployees.contains(e)) {
                    pmEmployees.add(e);
                }
            }
        }
        return pmEmployees;
    }

    private List<Project> extractProjectsFromContributions(List<Contribution> contributions) {
        List<Project> projects = new ArrayList<>();
        for (Contribution c: contributions) {
            projects.add(c.getProject());
        }
        return projects;
    }

}
