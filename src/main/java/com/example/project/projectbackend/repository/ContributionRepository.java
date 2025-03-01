package com.example.project.projectbackend.repository;

import com.example.project.projectbackend.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Integer> {
    @Query("SELECT c FROM Contribution c WHERE c.project.id_project = :projectId")
    List<Contribution> findByProjectId(@Param("projectId") Integer projectId);
    List<Contribution> findAll();
}