package tn.esprit.pidev.services;

import org.springframework.stereotype.Service;
import tn.esprit.pidev.dto.LoanSimulationRequest;
import tn.esprit.pidev.dto.LoanSimulationResponse;

@Service
public class LoanSimulationService {

    public LoanSimulationResponse simulateLoan(LoanSimulationRequest request) {
        double amount = request.getAmount();
        int duration = request.getDuration();
        double interestRate = request.getInterestRate();

        // Conversion du taux d'intérêt annuel en taux mensuel
        double monthlyRate = (interestRate / 100) / 12;

        // Calcul de la mensualité avec la formule de l'annuité
        double monthlyPayment = (amount * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -duration));

        // Calcul du coût total et des intérêts
        double totalAmount = monthlyPayment * duration;
        double totalInterest = totalAmount - amount;

        return new LoanSimulationResponse(monthlyPayment, totalInterest, totalAmount);
    }
}
