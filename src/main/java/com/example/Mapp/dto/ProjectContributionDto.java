package com.example.Mapp.dto;

import com.example.Mapp.model.Contribution;
import com.example.Mapp.model.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectContributionDto {
    private Project project;
    private Contribution contribution;
}
