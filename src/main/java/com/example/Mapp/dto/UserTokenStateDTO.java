package com.example.Mapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTokenStateDTO {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String role;
    private String userId;
}
