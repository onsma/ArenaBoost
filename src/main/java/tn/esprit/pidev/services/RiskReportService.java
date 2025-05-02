package tn.esprit.pidev.services;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Risk;
import tn.esprit.pidev.repositories.RiskRepository;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
@Service
public class RiskReportService {
    private final RiskRepository riskRepository;

    public RiskReportService(RiskRepository riskRepository) {
        this.riskRepository = riskRepository;
    }

    public byte[] generateRiskReport() throws IOException {
        // Create a ByteArrayOutputStream to store PDF data
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Initialize PDF Writer and Document
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Add a title
        document.add(new Paragraph("Risk Report").setBold().setFontSize(18));

        // Fetch all risks from DB
        List<Risk> risks = riskRepository.findAll();

        // Add risk details to the PDF
        for (Risk risk : risks) {
            document.add(new Paragraph("Risk ID: " + risk.getRisk_id()));
            document.add(new Paragraph("Score: " + risk.getScore()));
            document.add(new Paragraph("Probability: " + risk.getProbability()));
            document.add(new Paragraph("Impact: " + risk.getImpact()));
            document.add(new Paragraph("Risk Type: " + risk.getRisktype()));
            document.add(new Paragraph("Amount: " + risk.getAmount()));
            document.add(new Paragraph("-----------------------------------"));
        }

        // Close the document
        document.close();

        return outputStream.toByteArray(); // Return the PDF as bytes
    }
}
