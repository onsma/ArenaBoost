package tn.esprit.pidev.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidev.entities.Risk;
import tn.esprit.pidev.repositories.RiskRepository;
import tn.esprit.pidev.services.IRiskService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.pidev.services.RiskReportService;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/risk")
public class RiskController {
    private RiskRepository riskRepository;
    IRiskService riskService;
    private final RiskReportService riskReportService;


    @GetMapping("/getRisk")
    public List<Risk> getallRisks () {
        return riskService.getAllRisks();
    }

    @PostMapping("/AddRisk")
    public Risk addRisk(@RequestBody Risk risk) {
        return riskService.AddRisk(risk);
    }

    @DeleteMapping("deleteRisk/{risk_id}")
    public void deleteRisk(@PathVariable Long risk_id) {
        riskService.deleteRisk(risk_id);
    }

    @PutMapping("/updateRisk/{id}")
    public Risk updateRisk(@PathVariable Long id, @RequestBody Risk risk) {
        return riskService.updateRisk(id, risk);
    }



    @GetMapping("/calculateScore/{id}")
    public double calculateRiskScore(@PathVariable Long id) {
        return riskService.calculateRiskScore(id);
    }
    @PostMapping("/checkRisk/{riskId}")
    public ResponseEntity<String> checkRiskAndNotify(@PathVariable Long riskId) {
        try {
            riskService.calculateAndNotify(riskId);
            return ResponseEntity.ok("Risk score calculated, notification sent if needed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/generateReport")
    public ResponseEntity<byte[]> generateReport() throws IOException {
        byte[] pdfContent = riskReportService.generateRiskReport();

        // Set headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=risk_report.pdf");
        headers.add("Content-Type", "application/pdf");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}