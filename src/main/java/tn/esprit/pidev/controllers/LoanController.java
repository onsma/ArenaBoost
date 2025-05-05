package tn.esprit.pidev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidev.dto.LoanSearchCriteria;
import tn.esprit.pidev.dto.LoanStatisticsDTO;
import tn.esprit.pidev.dto.LoanUpdateDTO;
import tn.esprit.pidev.entities.Loan;
import tn.esprit.pidev.entities.Loantype;
import tn.esprit.pidev.entities.Status;
import tn.esprit.pidev.services.LoanService;
import tn.esprit.pidev.services.MailService;
import tn.esprit.pidev.services.PdfService;

import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> updateLoan(@PathVariable Long id, @RequestBody LoanUpdateDTO loanUpdateDTO) {
        try {
            // Récupérer le prêt existant
            Optional<Loan> existingLoanOpt = loanService.getLoanById(id);
            if (existingLoanOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Loan existingLoan = existingLoanOpt.get();

            // Ajout de logs pour déboguer
            System.out.println("Données reçues pour mise à jour du prêt #" + id + ":");
            System.out.println("- Montant: " + loanUpdateDTO.getAmount());
            System.out.println("- Type de prêt: " + loanUpdateDTO.getLoantype());
            System.out.println("- Taux d'intérêt: " + loanUpdateDTO.getInterest_rate());
            System.out.println("- Durée: " + loanUpdateDTO.getRefund_duration());
            System.out.println("- Statut: " + loanUpdateDTO.getStatus());

            // Mettre à jour les champs du prêt
            existingLoan.setAmount(loanUpdateDTO.getAmount());
            existingLoan.setInterest_rate(loanUpdateDTO.getInterest_rate());
            existingLoan.setRefund_duration(loanUpdateDTO.getRefund_duration());

            // Traitement spécial pour le type de prêt
            try {
                Loantype loantype = Loantype.valueOf(loanUpdateDTO.getLoantype());
                existingLoan.setLoantype(loantype);
            } catch (IllegalArgumentException e) {
                System.err.println("Type de prêt invalide: " + loanUpdateDTO.getLoantype());
            }

            // Traitement spécial pour le statut
            try {
                String statusStr = loanUpdateDTO.getStatus();
                System.out.println("- Statut reçu: " + statusStr);

                // Convertir en minuscules pour correspondre à l'enum Status
                Status status = Status.valueOf(statusStr.toLowerCase());
                System.out.println("- Statut converti: " + status);
                existingLoan.setStatus(status);
            } catch (IllegalArgumentException e) {
                System.err.println("Statut invalide: " + loanUpdateDTO.getStatus());
                e.printStackTrace();
            }

            // Mettre à jour le prêt
            Loan updatedLoan = loanService.updateLoan(id, existingLoan);

            // Log du prêt mis à jour
            System.out.println("Prêt mis à jour avec succès:");
            System.out.println("- ID: " + updatedLoan.getId_loan());
            System.out.println("- Montant: " + updatedLoan.getAmount());
            System.out.println("- Type de prêt: " + updatedLoan.getLoantype());
            System.out.println("- Taux d'intérêt: " + updatedLoan.getInterest_rate());
            System.out.println("- Durée: " + updatedLoan.getRefund_duration());
            System.out.println("- Statut: " + updatedLoan.getStatus());

            return ResponseEntity.ok(updatedLoan);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du prêt #" + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Met à jour uniquement le statut d'un prêt
     * @param id L'ID du prêt à mettre à jour
     * @param statusMap Map contenant le nouveau statut
     * @return Le prêt mis à jour
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateLoanStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        try {
            // Vérifier que l'ID est valide
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID de prêt invalide");
            }

            // Vérifier que le statut est fourni
            String newStatus = statusMap.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Le statut est requis");
            }

            System.out.println("Mise à jour du statut du prêt #" + id + " vers: " + newStatus);
            System.out.println("Données reçues: " + statusMap);

            // Vérifier que le prêt existe
            Optional<Loan> loanOpt = loanService.getLoanById(id);
            if (loanOpt.isEmpty()) {
                System.err.println("Prêt non trouvé avec l'ID: " + id);
                return ResponseEntity.status(404).body("Prêt non trouvé avec l'ID: " + id);
            }

            // Mettre à jour le statut
            Loan updatedLoan = loanService.updateLoanStatus(id, newStatus);

            System.out.println("Statut mis à jour avec succès: " + updatedLoan.getStatus());
            return ResponseEntity.ok(updatedLoan);
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de la mise à jour du statut du prêt #" + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur lors de la mise à jour du statut: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de la mise à jour du statut du prêt #" + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur inattendue: " + e.getMessage());
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
