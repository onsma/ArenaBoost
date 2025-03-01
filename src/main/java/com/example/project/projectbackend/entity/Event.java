package com.example.project.projectbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_event;
    private String name;
    private int ticket_price;
    private int tickets_sold;
    private float total_revenue;

    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;

    // Calculer les revenus totaux
    public void updateTotalRevenue() {
        this.total_revenue = ticket_price * tickets_sold;
    }
}