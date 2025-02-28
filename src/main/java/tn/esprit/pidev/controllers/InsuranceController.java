package tn.esprit.pidev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidev.entities.Insurance;
import tn.esprit.pidev.services.InsuranceService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/insurance")
public class InsuranceController {

    @Autowired
    private InsuranceService insuranceService;

    // Create
    @PostMapping
    public ResponseEntity<Insurance> createInsurance(@RequestBody Insurance insurance) {
        Insurance created = insuranceService.createInsurance(insurance);
        return ResponseEntity.ok(created);
    }

    // Read all
    @GetMapping
    public List<Insurance> getAllInsurances() {
        return insuranceService.getAllInsurances();
    }

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<Insurance> getInsuranceById(@PathVariable long id) {
        Insurance insurance = insuranceService.getInsuranceById(id);
        return ResponseEntity.ok(insurance);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Insurance> updateInsurance(@PathVariable long id, @RequestBody Insurance updatedInsurance) {
        Insurance result = insuranceService.updateInsurance(id, updatedInsurance);
        return ResponseEntity.ok(result);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsurance(@PathVariable long id) {
        insuranceService.deleteInsurance(id);
        return ResponseEntity.noContent().build();
    }
}
