package tn.esprit.pidev.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Insurance;
import tn.esprit.pidev.repositories.InsuranceRepository;

import java.util.List;

@Service
public class InsuranceService {

    @Autowired
    private InsuranceRepository insuranceRepository;

    // Create
    public Insurance createInsurance(Insurance insurance) {
        // Additional business logic (eligibility checks, premium calc, etc.)
        return insuranceRepository.save(insurance);
    }

    // Read all
    public List<Insurance> getAllInsurances() {
        return insuranceRepository.findAll();
    }

    // Read by ID
    public Insurance getInsuranceById(long id) {
        return insuranceRepository.findById(id).get();
    }

    // Update
    public Insurance updateInsurance(long id, Insurance updatedInsurance) {
        Insurance existing = getInsuranceById(id);
        // Update fields
        existing.setTypeInsurance(updatedInsurance.getTypeInsurance());
        existing.setAmount(updatedInsurance.getAmount());
        existing.setSubscription_date(updatedInsurance.getSubscription_date());
        existing.setRenewal_date(updatedInsurance.getRenewal_date());
        existing.setStatusInsurance(updatedInsurance.getStatusInsurance());
//        existing.setUser(updatedInsurance.getUser());
        // ... other fields or logic
        return insuranceRepository.save(existing);
    }

    // Delete
    public void deleteInsurance(long id) {
        Insurance existing = getInsuranceById(id);
        // Additional checks (penalties, cancellation conditions, etc.)
        insuranceRepository.delete(existing);
    }
}
