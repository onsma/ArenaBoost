package tn.esprit.pidev.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Supporter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_supporter;
    private String phone_number;
    private String favourite_club;
}
