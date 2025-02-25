package com.example.project.projectbackend.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private  float goal_amount;
    private float current_amount;


}
