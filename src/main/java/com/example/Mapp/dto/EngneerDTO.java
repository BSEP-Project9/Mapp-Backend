package com.example.Mapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EngneerDTO {

    private Long id;

    private String email;

    private String surname;

    private String name;

//    private String role;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startOfEmployment;

}
