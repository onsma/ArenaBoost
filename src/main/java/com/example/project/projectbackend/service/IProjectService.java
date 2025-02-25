package com.example.project.projectbackend.service;

import com.example.project.projectbackend.entity.Project;
import java.util.List;

public interface IProjectService {
    List<Project> retrieveAllProjects();
    Project retrieveProject(Integer projectId);
    Project addProject(Project p);
    void removeProject(Integer projectId);
    Project modifyProject(Project project);
    List<Project> addListProject(List<Project> projects);
}