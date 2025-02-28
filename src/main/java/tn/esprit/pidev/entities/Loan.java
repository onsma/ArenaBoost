package tn.esprit.pidev.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_loan;
    private float amount;
    private float interest_rate;
    private int refund_duration;
    private Status status;
    //private int id_user;
    @ManyToOne
    @JoinColumn(name = "id_athelte")
    private Athlete athlete; // L'utilisateur qui a demandé ou financé le prêt

    @OneToMany(mappedBy = "loan")  // Un prêt peut avoir plusieurs transactions
    private List<Transaction> transactions;  // Liste des transactions associées au prêt
}
