package tn.esprit.pidev.services;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.borders.Border;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Insurance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfService {

    public byte[] generateInsurancePdfWithBackgroundAndLogo(Insurance insurance) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // --- 1) Add background image (on the first page) ---
            addBackgroundImage(pdfDoc, document);

            // --- 2) Add logo at the top of the page ---
            addLogo(pdfDoc, document);

            // --- 3) Add a title below the logo ---
            Paragraph title = new Paragraph("Insurance Policy Details")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(18)
                    .setMarginTop(100); // some spacing below the logo
            document.add(title);

            // --- 4) Build the details table ---
            Table table = new Table(new float[]{4, 6});
            table.setWidth(UnitValue.createPercentValue(80)) // narrower table, 80% of page width
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginTop(20)
                    .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

            // Row: Policy Type
            table.addCell(new Cell().add(new Paragraph("Policy Type:").setBold()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(insurance.getTypeInsurance()))).setBorder(Border.NO_BORDER));

            // Row: Coverage Amount
            table.addCell(new Cell().add(new Paragraph("Coverage Amount:").setBold()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(insurance.getAmount()))).setBorder(Border.NO_BORDER));

            // Row: Subscription Date
            table.addCell(new Cell().add(new Paragraph("Subscription Date:").setBold()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(insurance.getSubscription_date()))).setBorder(Border.NO_BORDER));

            // Row: Renewal Date
            table.addCell(new Cell().add(new Paragraph("Renewal Date:").setBold()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(insurance.getRenewal_date()))).setBorder(Border.NO_BORDER));

            // Row: Status
            table.addCell(new Cell().add(new Paragraph("Status:").setBold()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(insurance.getStatusInsurance()))).setBorder(Border.NO_BORDER));

            // Add table to document
            document.add(table);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF with background/logo", e);
        }
    }

    /**
     * Adds a semi-transparent background image covering the entire page.
     */
    private void addBackgroundImage(PdfDocument pdfDoc, Document document) throws IOException {
        // Load background image from resources
        ClassPathResource bgResource = new ClassPathResource("backgrounds/insurance_bg.jpg");
        ImageData bgImageData = ImageDataFactory.create(bgResource.getInputStream().readAllBytes());
        Image bgImage = new Image(bgImageData);

        // Make it cover the entire page, with partial opacity
        float pageWidth = pdfDoc.getDefaultPageSize().getWidth();
        float pageHeight = pdfDoc.getDefaultPageSize().getHeight();
        bgImage.setOpacity(0.2f); // adjust opacity as needed
        bgImage.scaleAbsolute(pageWidth, pageHeight);
        bgImage.setFixedPosition(0, 0);

        // Add to document
        document.add(bgImage);
    }

    /**
     * Adds a logo at the top (left or center).
     */
    private void addLogo(PdfDocument pdfDoc, Document document) throws IOException {
        // Load logo from resources
        ClassPathResource logoResource = new ClassPathResource("images/company_logo.png");
        ImageData logoData = ImageDataFactory.create(logoResource.getInputStream().readAllBytes());
        Image logo = new Image(logoData);

        // Adjust logo size and position
        float logoWidth = 100; // your desired width
        float logoHeight = 100; // your desired height
        logo.scaleAbsolute(logoWidth, logoHeight);

        // We can place the logo at the top center:
        float x = (pdfDoc.getDefaultPageSize().getWidth() - logoWidth) / 2;
        float y = pdfDoc.getDefaultPageSize().getHeight() - logoHeight - 20; // 20 px from top

        logo.setFixedPosition(x, y);

        document.add(logo);
    }
}
