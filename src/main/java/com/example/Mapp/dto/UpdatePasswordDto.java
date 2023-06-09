package com.example.Mapp.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdatePasswordDto {
    private String email;
    private String updatedPassword;
}
