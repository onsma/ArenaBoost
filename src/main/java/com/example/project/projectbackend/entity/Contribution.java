package com.example.project.projectbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Contribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_contribution;
    private float amount;
    private String supporter_name;
    private String reward;
    private String email; // Nouveau champ pour l'e-mail du supporter

    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;
}