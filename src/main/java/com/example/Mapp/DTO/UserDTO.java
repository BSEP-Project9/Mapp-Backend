package com.example.Mapp.DTO;

import com.example.Mapp.model.Address;
import com.example.Mapp.model.Contribution;
import com.example.Mapp.model.Role;
import com.example.Mapp.model.Skill;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String email;
    //@Min(4)
    private String password;
    //@Min(4)
    private String rPassword;

    private String name;

    private String surname;

    private String phoneNumber;

    private String role;

    private AddressDTO address;
}
