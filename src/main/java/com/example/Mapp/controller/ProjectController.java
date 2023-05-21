package com.example.Mapp.controller;

import com.example.Mapp.model.Project;
import com.example.Mapp.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<Project> getAll(){
        return projectService.getAll();
    }

    @PostMapping
    public Project create(@RequestBody Project project) {
        return projectService.create(project);
    }

}
