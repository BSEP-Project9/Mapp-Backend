package com.example.Mapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    private String email;

    private String password;

    private String name;

    private String surname;

    private String phoneNumber;

    private AddressDTO address;
}
