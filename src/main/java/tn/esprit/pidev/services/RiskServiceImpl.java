package tn.esprit.pidev.services;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Risk;
import tn.esprit.pidev.repositories.RiskRepository;

import java.util.List;



@Service
@AllArgsConstructor
public class RiskServiceImpl implements IRiskService {
    RiskRepository riskrep ;


    @Override
    public List<Risk> getAllRisks() {
        return riskrep.findAll();
    }

    @Override
    public Risk AddRisk(Risk risk) {
        return riskrep.save(risk);
    }

    @Override
    public void deleteRisk(Long risk_id) {
    riskrep.deleteById(risk_id);
    }

    @Override
    public Risk getRisk(Long risk_id) {
        return riskrep.findById(risk_id).get()  ;
    }

    @Override
    public Risk updateRisk(Long idR, Risk risk) {
        return riskrep.save(risk) ;
    }

    public double calculateRiskScore(Long risk_id) {
        Risk risk = riskrep.findById(risk_id).orElseThrow(() -> new RuntimeException("Risk not found"));
        return (risk.getProbability() * risk.getImpact()) / risk.getLoanAmount();
    }
    @Autowired
    private EmailService emailService;

    public void calculateAndNotify(Long riskId) {
        // Your existing logic to calculate the risk score
        Risk risk = riskrep.findById(riskId).orElseThrow(() -> new RuntimeException("Risk not found"));

        double score = calculateRiskScore(riskId);  // Assume this function calculates the risk score

        // Check if the risk score is higher than a threshold
        if (score > 1) {
            // Send email notification
            String subject = "High Risk Score Alert";
            String body = "The risk score for risk ID " + riskId + " has exceeded the threshold. The current score is " + score + ".";
            emailService.sendEmail("ons.matmati@gmail.com", subject, body);  // Send email
        }
    }

}
