package com.example.Mapp.controller;


import com.example.Mapp.dto.ContributionToDto;
import com.example.Mapp.model.Contribution;
import com.example.Mapp.model.User;
import com.example.Mapp.service.ContributionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contributions")
public class ContributionController {

    private final ContributionService contributionService;

    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Contribution> getAll() {
        return contributionService.getAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/project/{id}")
    public List<Contribution> getAllByProject(@PathVariable("id") Long projectId) {
        return contributionService.getAllByProject(projectId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/worker/{id}")
    public List<Contribution> getAllByWorker(@PathVariable("id") Long workerId) {
        return contributionService.getAllByWorker(workerId);
    }

    @PostMapping("/workers-to-project")
    public ResponseEntity<Void> addEmployeesToProject(@RequestBody ContributionToDto contributionDto) {
        contributionService.addEmployeesToProject(contributionDto);
        return ResponseEntity.ok().build();
    }


}
