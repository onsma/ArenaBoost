package com.example.project.projectbackend.service;

import com.example.project.projectbackend.entity.*;

import java.util.List;

public interface IProjectService {
    List<Project> retrieveAllProjects();
    Project retrieveProject(Integer projectId);
    Project addProject(Project p);
    void removeProject(Integer projectId);
    Project modifyProject(Project project);
    List<Project> addListProject(List<Project> projects);

    // Nouvelles méthodes
    float calculateTotalFundsCollected(Integer projectId);
    void addExpense(Integer projectId, float expenseAmount);
    float getProgressPercentage(Integer projectId);
    Contribution addContribution(Integer projectId, Contribution contribution);
    Event addEvent(Integer projectId, Event event);

    ProjectAnalytics getProjectAnalytics(Integer projectId);
    byte[] generateFinancialReport(Integer projectId);

    ProjectPrediction predictProjectOutcome(Integer projectId);

    List<Project> searchProjectsByName(String name); // Nouvelle méthode
}