package tn.esprit.pidev.controller;


import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidev.entities.*;
import tn.esprit.pidev.services.ExcelExportService;
import tn.esprit.pidev.services.InvestmentService;
import tn.esprit.pidev.services.InvestorService;
import lombok.NoArgsConstructor;
//import org.hibernate.cfg.Environment;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import tn.esprit.pidev.entities.Investment;
import tn.esprit.pidev.entities.Investor;
import tn.esprit.pidev.services.InvestorService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
//import javax.mail.MessagingException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import java.util.List;

@RestController
@RequestMapping("/investments")
public class InvestmentController {

    private static final String TEMPLATE_NAME = "investment-confirmation";
    private static final String SPRING_LOGO_IMAGE = "templates/images/spring.png";
    private static final String PNG_MIME = "image/png";
    private static final String MAIL_SUBJECT = "Investment Confirmation";

    private final Environment environment;
    private final JavaMailSender mailSender;
    private final TemplateEngine htmlTemplateEngine;

    public InvestmentController(Environment environment, JavaMailSender mailSender, TemplateEngine htmlTemplateEngine) {
        this.environment = environment;
        this.mailSender = mailSender;
        this.htmlTemplateEngine = htmlTemplateEngine;
    }

//    @PostMapping("/confirm-investment")
//    public ResponseEntity<Object> confirmInvestment(@RequestBody Investment investment)
//            throws MessagingException, UnsupportedEncodingException {
//
//        Investor investor = investment.getInvestor();
//
//        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
//        String mailFromName = environment.getProperty("mail.from.name", "ArenaBoost");
//
//        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
//        final MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//
//        email.setTo(investor.getEmail());
//        email.setSubject(MAIL_SUBJECT);
//        email.setFrom(new InternetAddress(mailFrom, mailFromName));
//
//        // Convert `Date` to `LocalDateTime`
//        LocalDateTime createdAt = investor.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//
//        // Format the date
//        String formattedDate = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//
//        final Context ctx = new Context(LocaleContextHolder.getLocale());
//        ctx.setVariable("investorId", investor.getInvestorId());
//        ctx.setVariable("name", investor.getName());
//        ctx.setVariable("email", investor.getEmail());
//        ctx.setVariable("phone", investor.getPhone());
//        ctx.setVariable("investmentBudget", investor.getInvestment_budget());
//        ctx.setVariable("riskTolerance", investor.getRiskTolerance());
//        ctx.setVariable("createdAt", formattedDate);
//
//        ctx.setVariable("investmentId", investment.getInvestmentId());
//        ctx.setVariable("investmentType", investment.getAthlete() != null ? "Athlete" : "Project");
//        ctx.setVariable("amount", investment.getAmount());
//        ctx.setVariable("description", investment.getDescription());
//        ctx.setVariable("isActive", investment.isActive() ? "Yes" : "No");
//        ctx.setVariable("status", investment.getStatus());
//
//        ctx.setVariable("springLogo", SPRING_LOGO_IMAGE);
//
//        final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);
//        email.setText(htmlContent, true);
//
//        ClassPathResource clr = new ClassPathResource(SPRING_LOGO_IMAGE);
//        email.addInline("springLogo", clr, PNG_MIME);
//
//        mailSender.send(mimeMessage);
//
//        Map<String, String> body = new HashMap<>();
//        body.put("message", "Investment confirmation email sent successfully.");
//
//        return new ResponseEntity<>(body, HttpStatus.OK);
//    }

    @Autowired
    InvestmentService investmentService;


    // Create a new investment
//    @PostMapping("/addInvestment")
//    public ResponseEntity<Investment> createInvestment(@RequestBody Investment investment) {
//        Investment createdInvestment = investmentService.saveInvestment(investment);
//        return ResponseEntity.ok(createdInvestment);
//    }

    @PostMapping("/addInvestment")
    public ResponseEntity<Investment> createInvestment(@RequestBody Investment investment)
            throws MessagingException, UnsupportedEncodingException {

        Investment createdInvestment = investmentService.saveInvestment(investment);

        sendInvestmentConfirmationEmail(createdInvestment);

        return ResponseEntity.ok(createdInvestment);
    }

    private void sendInvestmentConfirmationEmail(Investment investment) {
        // Extract investor details
        Investor investor = investment.getInvestor();

        // Create email details
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(investor.getEmail());
        emailDetails.setSubject("Investment Confirmation");
        emailDetails.setInvestment(investment);

        // Send the email
        investmentService.sendMail(emailDetails, investment, investor);
    }

    // Retrieve an investment by its ID
    @GetMapping("/getInvestment/{id}")
    public ResponseEntity<Investment> getInvestmentById(@PathVariable Long id) {
        Investment investment = investmentService.findInvestmentById(id);
        return ResponseEntity.ok(investment);
    }

    // Retrieve all investments
    @GetMapping("/getAllInvestment/")
    public ResponseEntity<List<Investment>> getAllInvestments() {
        List<Investment> investments = investmentService.findAllInvestments();
        return ResponseEntity.ok(investments);
    }

    // Update an existing investment
    @PutMapping("/editInvestment/{id}")
    public ResponseEntity<Investment> updateInvestment(@PathVariable Long id, @RequestBody Investment investment) {
        Investment updatedInvestment = investmentService.updateInvestment(id, investment);
        return ResponseEntity.ok(updatedInvestment);
    }

    // Delete an investment
    @DeleteMapping("/deleteInvestment/{id}")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public List<Investment> filterInvestments(
            @RequestParam(required = false) InvestmentType investmentType,
            @RequestParam(required = false) Double minROI,
            @RequestParam(required = false) Double maxROI,
            @RequestParam(required = false) InvestmentStatus status,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) DividendPaymentFrequency dividendPaymentFrequency,
            @RequestParam(required = false) Double minDividendRate,
            @RequestParam(required = false) Double maxDividendRate
    ) {
        return investmentService.findInvestmentsByCriteria(
                investmentType,
                minROI,
                maxROI,
                status,
                minAmount,
                maxAmount,
                dividendPaymentFrequency,
                minDividendRate,
                maxDividendRate
        );
    }

    @PutMapping("/{id}/calculate-roi")
    public ResponseEntity<Investment> calculateAndUpdateROI(
            @PathVariable long id,
            @RequestParam double netProfit) {
        Investment updatedInvestment = investmentService.calculateAndUpdateROI(id, netProfit);
        return ResponseEntity.ok(updatedInvestment);
    }

    @GetMapping("/{investmentId}/roi")
    public ResponseEntity<Double> getROIForInvestment(
            @PathVariable long investmentId) {
        Double roi = investmentService.getROIForInvestment(investmentId);
        return ResponseEntity.ok(roi);
    }

    @GetMapping("/investors/{investorId}/roi")
    public ResponseEntity<List<Investment>> getInvestmentWithROIForInvestor(
            @PathVariable long investorId) {
        List<Investment> investments = investmentService.getInvestmentWithROIForInvestor(investorId);
        return ResponseEntity.ok(investments);
    }

    @Autowired
    private ExcelExportService excelExportService;

    @GetMapping("/export/excel")
    public ResponseEntity<ByteArrayResource> exportInvestmentsToExcel() throws IOException {
        // Fetch all investments
        List<Investment> investments = investmentService.findAllInvestments();

        // Generate Excel file
        byte[] excelBytes = excelExportService.exportInvestmentsToExcel(investments);

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=investments.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // Return the file as a downloadable response
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(excelBytes.length)
                .body(new ByteArrayResource(excelBytes));
    }
//
//    @GetMapping("/export/excel/investor/{investorId}")
//    public ResponseEntity<ByteArrayResource> exportInvestmentsByInvestorId(@PathVariable Long investorId) throws IOException {
//        // Fetch investments for the specified investor
//        List<Investment> investments = investmentService.findInvestmentsByInvestorId(investorId);
//
//        // Generate Excel file
//        byte[] excelBytes = excelExportService.exportInvestmentsToExcel(investments);
//
//        // Optionally, save the file to a specific directory
//        String filePath = "C:/exports/investor_" + investorId + "_investments.xlsx"; // Specify your directory here
//        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
//            fileOut.write(excelBytes);
//        }
//
//        // Set response headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=investor_" + investorId + "_investments.xlsx");
//        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
//
//        // Return the file as a downloadable response
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentLength(excelBytes.length)
//                .body(new ByteArrayResource(excelBytes));
//    }

}
