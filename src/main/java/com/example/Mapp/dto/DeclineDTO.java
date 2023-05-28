package com.example.Mapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeclineDTO {

    private Long id;

    private String email;

    private String name;

    private String surname;

    private boolean isActivated;

    private String msg;
}
