package com.example.Mapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordlessLoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String redirectLocation;
}
