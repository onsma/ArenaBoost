package tn.esprit.pidev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidev.dto.LoanSimulationRequest;
import tn.esprit.pidev.dto.LoanSimulationResponse;
import tn.esprit.pidev.services.LoanSimulationService;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*") // Autoriser les requêtes depuis n'importe quel client
public class LoanSimulationController {

    @Autowired
    private LoanSimulationService loanSimulationService;

    @PostMapping("/simulate")
    public LoanSimulationResponse simulateLoan(@RequestBody LoanSimulationRequest request) {
        return loanSimulationService.simulateLoan(request);
    }
}
