package tn.esprit.pidev.entities;

import jakarta.persistence.*;
import jdk.jfr.Category;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_project;
    private String name;
    private String description;
    private category category;
    private float goal_amount;
    private float current_amount;
    //private long id_manager;
    //private long id_supoorter;
    private Date deadline;

    @ManyToOne
    @JoinColumn(name = "id_manager")
    private Manager manager;  // Un projet appartient à un manager

    @ManyToOne
    @JoinColumn(name = "id_supporter", nullable = true)
    private Supporter supporter;  // Un supporter peut aussi financer un projet

    public long getId_project() {
        return id_project;
    }

    public void setId_project(long id_project) {
        this.id_project = id_project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public category getCategory() {
        return category;
    }

    public void setCategory(category category) {
        this.category = category;
    }

    public float getGoal_amount() {
        return goal_amount;
    }

    public void setGoal_amount(float goal_amount) {
        this.goal_amount = goal_amount;
    }

    public float getCurrent_amount() {
        return current_amount;
    }

    public void setCurrent_amount(float current_amount) {
        this.current_amount = current_amount;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Supporter getSupporter() {
        return supporter;
    }

    public void setSupporter(Supporter supporter) {
        this.supporter = supporter;
    }
}
