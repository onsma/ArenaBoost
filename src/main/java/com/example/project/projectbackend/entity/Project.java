package com.example.project.projectbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_project;
    private String name;
    private String description;
    @Enumerated(value = EnumType.STRING)
    private Category category;
    private float goal_amount;
    private float current_amount;
    private float total_expenses;
    private String image;
    private int duration_days; // Nombre total de jours prévus pour le projet
    private int days_remaining; // Jours restants, mis à jour automatiquement
    private LocalDateTime start_date; // Date de début pour calculer dynamiquement

    // Méthode pour initialiser days_remaining à la création
    @PrePersist
    public void prePersist() {
        if (duration_days > 0 && start_date == null) {
            start_date = LocalDateTime.now();
            days_remaining = duration_days;
        }
    }

    // Méthode pour calculer les jours restants dynamiquement (optionnel, si vous voulez recalculer)
    public int calculateDaysRemaining() {
        if (start_date == null || duration_days <= 0) return 0;
        LocalDateTime now = LocalDateTime.now();
        long daysElapsed = java.time.temporal.ChronoUnit.DAYS.between(start_date, now);
        int remaining = duration_days - (int) daysElapsed;
        return Math.max(0, remaining); // Ne pas retourner un nombre négatif
    }

    // Méthode pour calculer le pourcentage de progression
    public float getProgressPercentage() {
        return (current_amount / goal_amount) * 100;
    }
}