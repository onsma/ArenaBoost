package com.example.project.projectbackend.controller;

import com.example.project.projectbackend.entity.Project;
import com.example.project.projectbackend.service.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectRestController {

    private final IProjectService projectService;

    @Autowired
    public ProjectRestController(IProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/retrieve-all-projects")
    public List<Project> getProjects() {
        return projectService.retrieveAllProjects();
    }

    @GetMapping("/retrieve-project/{project-id}")
    public Project retrieveProject(@PathVariable("project-id") Integer projectId) {
        return projectService.retrieveProject(projectId);
    }

    @PostMapping("/add-project")
    public Project addProject(@RequestBody Project p) {
        return projectService.addProject(p);
    }

    @DeleteMapping("/remove-project/{project-id}")
    public void removeProject(@PathVariable("project-id") Integer projectId) {
        projectService.removeProject(projectId);
    }

    @PutMapping("/modify-project")
    public Project modifyProject(@RequestBody Project p) {
        return projectService.modifyProject(p);
    }

    @PostMapping("/add-list-projects")
    public List<Project> addListProject(@RequestBody List<Project> projects) {
        return projectService.addListProject(projects);
    }
}