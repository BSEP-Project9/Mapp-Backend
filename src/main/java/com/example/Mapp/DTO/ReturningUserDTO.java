package com.example.Mapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturningUserDTO {

    private String email;

    private String name;

    private String surname;

    private String phoneNumber;

    private String role;

    private AddressDTO address;
}