package com.example.Mapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String email;

    private String password;

    private String name;

    private String surname;

    private String phoneNumber;

    private String role;

    private AddressDTO address;
}
