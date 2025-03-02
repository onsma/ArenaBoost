package tn.esprit.pidev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tn.esprit.pidev.dto.LoanStatisticsDTO;
import tn.esprit.pidev.services.LoanService;

@Controller
public class LoanStatisticsController {

    @Autowired
    private LoanService loanService;

    @GetMapping("/loan-statistics")
    public String showLoanStatistics(Model model) {
        LoanStatisticsDTO stats = loanService.getLoanStatistics();
        model.addAttribute("stats", stats);
        return "loanStatistics"; // Va charger loanStatistics.html
    }
}
