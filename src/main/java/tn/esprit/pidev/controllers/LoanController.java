package tn.esprit.pidev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidev.dto.LoanSearchCriteria;
import tn.esprit.pidev.dto.LoanStatisticsDTO;
import tn.esprit.pidev.entities.Loan;
import tn.esprit.pidev.services.LoanService;
import tn.esprit.pidev.services.MailService;
import tn.esprit.pidev.services.PdfService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final MailService mailService; // Activation du mailing

    @Autowired
    public LoanController(LoanService loanService, MailService mailService) {
        this.loanService = loanService;
        this.mailService = mailService;
    }

    @PostMapping("/{id_user}")
    public ResponseEntity<?> createLoan(@RequestBody Loan loan, @PathVariable Long id_user) {
        try {
            Loan createdLoan = loanService.createLoan(loan, id_user);
            return ResponseEntity.ok(createdLoan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        Optional<Loan> loan = loanService.getLoanById(id);
        return loan.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        List<Loan> loans = loanService.getAllLoans();
        return ResponseEntity.ok(loans);
    }
    @Autowired
    private PdfService pdfService;

    // 📌 Endpoint pour télécharger le PDF de l'historique des prêts
    @GetMapping("/{userId}/pdf")
    public ResponseEntity<byte[]> getUserLoanHistoryPdf(@PathVariable Long userId) {
        byte[] pdfBytes = pdfService.generateUserLoanHistory(userId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Historique_Pret.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLoan(@PathVariable Long id, @RequestBody Loan loanDetails) {
        try {
            Loan updatedLoan = loanService.updateLoan(id, loanDetails);

            // 📩 Envoi automatique d'un e-mail après mise à jour du prêt
            mailService.envoyerNotificationLoan(updatedLoan);

            return ResponseEntity.ok(updatedLoan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        boolean deleted = loanService.deleteLoan(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    // ✅ Endpoint pour récupérer les statistiques des prêts
    @GetMapping("/statistics")
    public ResponseEntity<LoanStatisticsDTO> getLoanStatistics() {
        LoanStatisticsDTO stats = loanService.getLoanStatistics();
        return ResponseEntity.ok(stats);
    }
    @PostMapping("/search")
    public ResponseEntity<List<Loan>> searchLoans(@RequestBody LoanSearchCriteria criteria) {
        List<Loan> loans = loanService.searchLoans(criteria);
        return ResponseEntity.ok(loans);
    }
}
