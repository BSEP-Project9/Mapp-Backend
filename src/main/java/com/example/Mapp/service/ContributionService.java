package com.example.Mapp.service;

import com.example.Mapp.model.Contribution;
import com.example.Mapp.repository.ContributionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContributionService {

    private final ContributionRepository contributionRepository;

    public ContributionService(ContributionRepository contributionRepository) {
        this.contributionRepository = contributionRepository;
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

}
