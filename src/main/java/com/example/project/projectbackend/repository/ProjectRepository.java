package com.example.project.projectbackend.repository;

import com.example.project.projectbackend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findByNameContainingIgnoreCase(String name); // Recherche insensible à la casse par nom
}
