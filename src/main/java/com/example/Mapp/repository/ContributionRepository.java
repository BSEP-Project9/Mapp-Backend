package com.example.Mapp.repository;

import com.example.Mapp.model.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    public List<Contribution> findByProjectId(Long projectId);

    public List<Contribution> findByWorkerId(Long workerId);

}
