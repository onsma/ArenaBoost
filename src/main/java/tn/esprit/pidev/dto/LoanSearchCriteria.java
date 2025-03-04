package tn.esprit.pidev.dto;


import lombok.Getter;
import lombok.Setter;
import tn.esprit.pidev.entities.Loantype;
import tn.esprit.pidev.entities.Status;

@Getter
@Setter
public class LoanSearchCriteria {
    private Double minAmount;      // Montant minimum
    private Double maxAmount;      // Montant maximum
    private Loantype loantype;     // Type de prêt
    private Double minInterestRate;// Taux d’intérêt min
    private Double maxInterestRate;// Taux d’intérêt max
    private Integer minDuration;   // Durée minimum
    private Integer maxDuration;   // Durée maximum
    private Status status;         // Statut du prêt
    private String userKeyword;    // Nom ou email du demandeur
}

