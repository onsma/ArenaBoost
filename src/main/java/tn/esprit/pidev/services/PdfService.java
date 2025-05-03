package tn.esprit.pidev.services;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Loan;
import tn.esprit.pidev.entities.User;
import tn.esprit.pidev.repositories.LoanRepository;
import tn.esprit.pidev.repositories.UserRepository;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

@Service
public class PdfService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;

    public byte[] generateUserLoanHistory(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Utilisateur introuvable !");
        }

        User user = userOptional.get();
        List<Loan> loans = loanRepository.findByUser(user); //  récupérer les prêts de l'utilisateur

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // 📌 Ajout du titre
            document.add(new Paragraph("Historique des prêts de " + user.getFirstName() + " " + user.getLastName())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(16));

            // 📌 Création du tableau
            Table table = new Table(new float[]{4, 4, 3, 3}); // Définition des colonnes
            table.setWidth(UnitValue.createPercentValue(100)); // Table sur toute la largeur

            // 📌 Entête du tableau
            table.addHeaderCell(new Cell().add(new Paragraph("Montant (TND)").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Type de prêt").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Durée (mois)").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Statut").setBold()));

            // 📌 Ajout des lignes de prêts
            for (Loan loan : loans) {
                table.addCell(String.valueOf(loan.getAmount())); // Montant du prêt
                table.addCell(loan.getLoantype().name()); // Type du prêt
                table.addCell(String.valueOf(loan.getRefund_duration())); // Durée du prêt
                table.addCell(loan.getStatus().name()); // Statut du prêt
            }

            // Ajout du tableau au document
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
}
