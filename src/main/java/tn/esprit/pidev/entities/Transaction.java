package tn.esprit.pidev.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_transaction;
    private Date date;
    private float amount;
    private long id_investment;
    //private long id_loan;
    private String description;
    @ManyToOne
    @JoinColumn(name = "id_loan")  // Associe la transaction à un prêt
    private Loan loan;  // Le prêt lié à la transaction

    @ManyToOne
    @JoinColumn(name = "investment_id")  // Associe la transaction à un investissement
    private Investment investment;  // L'investissement lié à la transaction

}
