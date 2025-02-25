package com.example.project.projectbackend.service;

import com.example.project.projectbackend.entity.Project;
import com.example.project.projectbackend.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements IProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public List<Project> retrieveAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project retrieveProject(Integer projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }

    @Override
    public Project addProject(Project p) {
        return projectRepository.save(p);
    }

    @Override
    public void removeProject(Integer projectId) {
        projectRepository.deleteById(projectId);
    }

    @Override
    public Project modifyProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public List<Project> addListProject(List<Project> projects) {
        return projectRepository.saveAll(projects);
    }
}