package com.example.Mapp.dto;

import com.example.Mapp.model.Address;
import com.example.Mapp.model.Contribution;
import com.example.Mapp.model.Role;
import com.example.Mapp.model.Skill;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String email;
    @Min(4)
    private String password;
    @Min(4)
    private String confirmPassword;

    private String name;

    private String surname;

    private String phoneNumber;

    private String role;

    private AddressDTO address;

    private List<Skill> skills;

}
