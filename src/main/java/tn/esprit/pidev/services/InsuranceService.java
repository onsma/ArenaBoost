package tn.esprit.pidev.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Insurance;
import tn.esprit.pidev.entities.User;
import tn.esprit.pidev.repositories.InsuranceRepository;
import tn.esprit.pidev.utils.EmailServiceInsurance;

import java.util.List;

@Service
public class InsuranceService {

    @Autowired
    private InsuranceRepository insuranceRepository;
    @Autowired
    private EmailServiceInsurance emailServiceInsurance;


    // Create
    public Insurance createInsurance(Insurance insurance) {
        Insurance savedInsurance = insuranceRepository.save(insurance);

        // 2. Check if user is present and has an email
        User user = savedInsurance.getUser();
        System.out.println("**************************************"+user);
        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
            // 3. Send the email asynchronously
            emailServiceInsurance.sendInsuranceEmail(user.getEmail(), savedInsurance);
        }

        return savedInsurance;
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
