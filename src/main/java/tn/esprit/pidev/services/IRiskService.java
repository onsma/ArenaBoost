package tn.esprit.pidev.services;

import tn.esprit.pidev.entities.Risk;

import java.util.List;

public interface IRiskService {
    public List<Risk> getAllRisks();
    public Risk AddRisk(Risk risk);
    public void deleteRisk(Long risk_id) ;
    public Risk getRisk (Long risk_id);
    public Risk updateRisk(Long idR, Risk risk);
    public double calculateRiskScore(Long risk_id);
    public void calculateAndNotify(Long riskId);
}
