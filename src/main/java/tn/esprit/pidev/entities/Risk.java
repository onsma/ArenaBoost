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
public class Risk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long risk_id;
    private int score;
    private float probability;
    private int impact;
    private Date last_date;
    private Description description;
    private RiskType risktype;
    //private long loan_id;
    private float amount ;
    @OneToOne
    @JoinColumn(name = "id_loan")  // Une colonne qui lie le prêt au risque
    private Loan loan;  // Le prêt auquel ce risque est associé

    public float getLoanAmount() {
        return this.amount;
    }

}
