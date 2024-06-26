package com.example.Mapp.controller;


import com.example.Mapp.dto.ContributionToDto;
import com.example.Mapp.dto.ProjectContributionDto;
import com.example.Mapp.model.Contribution;
import com.example.Mapp.model.Project;
import com.example.Mapp.model.User;
import com.example.Mapp.service.ContributionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/contributions")
@CrossOrigin(origins = "http://localhost:4200")
public class ContributionController {

    private final ContributionService contributionService;

    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @Secured({"ROLE_ADMIN"})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Contribution> getAll() {
        return contributionService.getAll();
    }

    @Secured({"ROLE_ADMIN", "ROLE_PM"})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/project/{id}")
    public List<Contribution> getAllByProject(@PathVariable("id") Long projectId) {
        return contributionService.getAllByProject(projectId);
    }

    @Secured({"ROLE_ADMIN", "ROLE_PM"})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/worker/{id}")
    public List<Contribution> getAllByWorker(@PathVariable("id") Long workerId) {
        return contributionService.getAllByWorker(workerId);
    }

    @Secured({"ROLE_ADMIN", "ROLE_PM"})
    @PostMapping("/workers-to-project")
    public ResponseEntity<Void> addEmployeesToProject(@RequestBody ContributionToDto contributionDto) {
        contributionService.addEmployeesToProject(contributionDto);
        return ResponseEntity.ok().build();
    }

    @Secured({"ROLE_ADMIN", "ROLE_PM"})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/worker/project/{id}")
    public List<User> getAllWorkerByProject(@PathVariable("id") Long projectId) {
        return contributionService.getAllWorkerByProject(projectId);
    }

    @Secured({"ROLE_ADMIN", "ROLE_PM"})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/project/worker/{id}")
    public List<ProjectContributionDto> getAllProjectByWorker(@PathVariable("id") Long workerId) {
        return contributionService.getAllProjectByWorker(workerId);
    }

    @Secured("ROLE_PM")
    @DeleteMapping("/workers-from-project/{workerId}/{projectId}")
    public ResponseEntity<Void> deleteEmployeesFromProject(@PathVariable("workerId") Long workerId, @PathVariable("projectId") Long projectId) {
        contributionService.deleteEmployeesFromProject(workerId, projectId);
        return ResponseEntity.ok().build();
    }

    @Secured({"ROLE_PM", "ROLE_HR"})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/projects/worker/{id}")
    public List<Project> getAllProjectsByWorker(@PathVariable("id") Long workerId) {
        return contributionService.getAllProjectsByWorker(workerId);
    }

}
