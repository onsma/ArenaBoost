package com.example.project.projectbackend.entity;

import lombok.Data;

@Data
public class ProjectPrediction {
    private boolean willReachGoal;  // Classification
    private float predictedFunds;   // Régression
}