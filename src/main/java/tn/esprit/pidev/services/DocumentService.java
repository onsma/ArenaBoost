package tn.esprit.pidev.services;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Loan;
import tn.esprit.pidev.repositories.LoanRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private LoanRepository loanRepository;

    public File generateLoanContract(Long loanId) throws IOException {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);

        if (!optionalLoan.isPresent()) {
            throw new RuntimeException("🚨 Prêt non trouvé avec l'ID : " + loanId);
        }

        Loan loan = optionalLoan.get();
        String filePath = "contracts/loan_contract_" + loanId + ".pdf";

        // 📌 Création du fichier PDF
        File file = new File(filePath);
        file.getParentFile().mkdirs(); // Créer le dossier s'il n'existe pas

        PdfWriter writer = new PdfWriter(new FileOutputStream(file));
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument, PageSize.A4);

        // ✅ Ajout de marges
        document.setMargins(40, 40, 40, 40);

        // 📌 1. **Ajout de l'En-tête avec un Logo**
        Paragraph header = new Paragraph("CONTRAT DE PRÊT")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLUE);
        document.add(header);

        // 📌 2. **Ajout d'une Ligne de Séparation**
        document.add(new LineSeparator(new SolidLine()));

        // 📌 3. **Ajout des Détails du Contrat**
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 7})).useAllAvailableWidth();

        table.addCell(new Cell().add(new Paragraph("Nom du Client :").setBold()));
        table.addCell(new Cell().add(new Paragraph(loan.getUser().getFirstName() + " " + loan.getUser().getLastName())));

        table.addCell(new Cell().add(new Paragraph("Montant du Prêt :").setBold()));
        table.addCell(new Cell().add(new Paragraph(loan.getAmount() + " €")));

        table.addCell(new Cell().add(new Paragraph("Durée :").setBold()));
        table.addCell(new Cell().add(new Paragraph(loan.getRefund_duration() + " mois")));

        table.addCell(new Cell().add(new Paragraph("Taux d’intérêt :").setBold()));
        table.addCell(new Cell().add(new Paragraph(loan.getInterest_rate() + " %")));

        // 📌 4. **Ajout de la Date de Signature**
        String formattedDate = loan.getRequestDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        table.addCell(new Cell().add(new Paragraph("Date de Signature :").setBold()));
        table.addCell(new Cell().add(new Paragraph(formattedDate)));

        document.add(table);

        // 📌 5. **Ajout d'une Ligne de Séparation**
        document.add(new LineSeparator(new SolidLine()));

        // 📌 6. **Ajout de la Signature**
        document.add(new Paragraph("\nSignature du Client : _______________________")
                .setTextAlignment(TextAlignment.RIGHT));

        // 📌 7. **Ajout du Pied de Page**
        document.add(new Paragraph("\n\nCe contrat est généré automatiquement et n'a pas besoin d'être signé physiquement.")
                .setFontSize(10)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY));

        // ✅ Fermeture du document
        document.close();
        System.out.println("📄 Contrat généré avec succès : " + filePath);

        return file;
    }
}
