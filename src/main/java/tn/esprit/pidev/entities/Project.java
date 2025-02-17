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
}
