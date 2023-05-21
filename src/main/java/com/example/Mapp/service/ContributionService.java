package com.example.Mapp.service;

import com.example.Mapp.dto.ContributionToDto;
import com.example.Mapp.model.Contribution;
import com.example.Mapp.model.Project;
import com.example.Mapp.model.User;
import com.example.Mapp.repository.ContributionRepository;
import com.example.Mapp.repository.ProjectRepository;
import com.example.Mapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ContributionService(ContributionRepository contributionRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.contributionRepository = contributionRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<Contribution> getAll() {
        return contributionRepository.findAll();
    }

    public List<Contribution> getAllByProject(Long projectId) {
        return contributionRepository.findByProjectId(projectId);
    }

    public List<Contribution> getAllByWorker(Long workerId) {
        return contributionRepository.findByWorkerId(workerId);
    }

    public void addEmployeesToProject(ContributionToDto contributionToDto) {
        Project project = projectRepository.findById(contributionToDto.getProjectId()).orElseThrow(() -> new EntityNotFoundException("Project didn't found"));
        List<User> workers = userRepository.findAllById(contributionToDto.getEmployeeIds());

        for (User worker : workers) {
            Contribution contribution = new Contribution();
            contribution.setWorker(worker);
            contribution.setProject(project);
            contributionRepository.save(contribution);
        }
    }
}
