package tn.esprit.pidev.controller;


import lombok.NoArgsConstructor;
//import org.hibernate.cfg.Environment;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import tn.esprit.pidev.entities.Athlete;
import tn.esprit.pidev.entities.Investment;
import tn.esprit.pidev.entities.Investor;
import tn.esprit.pidev.entities.Manager;
import tn.esprit.pidev.services.InvestorService;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

import java.util.List;


@RestController
//@NoArgsConstructor
@RequestMapping("/investors")
public class InvestorController {

    @Autowired
    private InvestorService investorService;

    // Create a new investor
    @PostMapping("/addInvestor")
    public ResponseEntity<Investor> createInvestor(@RequestBody Investor investor) {
        Investor createdInvestor = investorService.createInvestor(investor);
        return ResponseEntity.ok(createdInvestor);
    }

    // Retrieve an investor by ID
    @GetMapping("/getInvestor/{id}")
    public ResponseEntity<Investor> getInvestorById(@PathVariable Long id) {
        Investor investor = investorService.getInvestorById(id);
        return ResponseEntity.ok(investor);
    }

    // Retrieve all investors
    @GetMapping("getAllInvestors")
    public ResponseEntity<List<Investor>> getAllInvestors() {
        List<Investor> investors = investorService.getAllInvestor();
        return ResponseEntity.ok(investors);
    }

    // Update an investor
    @PutMapping("/updateInvestor/{id}")
    public ResponseEntity<Investor> updateInvestor(@PathVariable Long id, @RequestBody Investor investor) {
        Investor updatedInvestor = investorService.updateInvestor(id, investor);
        return ResponseEntity.ok(updatedInvestor);
    }

    // Delete an investor
    @DeleteMapping("/deleteInvestor/{id}")
    public ResponseEntity<Void> deleteInvestor(@PathVariable Long id) {
        investorService.deleteInvestor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/athletes/searchByName")
    public ResponseEntity<?> searchAthletesByName(@RequestParam(required = false) String name) {
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body("Name parameter is required.");
        }

        List<Athlete> athletes = investorService.searchAthletesByName(name);

        if (athletes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No athletes found with the given name.");
        }

        return ResponseEntity.ok(athletes);
    }

    // Search athletes by sport
    @GetMapping("/athletes/searchBySport")
    public ResponseEntity<?> searchAthletesBySport(@RequestParam String sport) {
        List<Athlete> athletes = investorService.searchAthletesBySport(sport);

        if (athletes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No athletes found in the given sport.");
        }

        return ResponseEntity.ok(athletes);
    }

    // Search managers by name
    @GetMapping("managers/searchByName")
    public ResponseEntity<?> searchManagersByName(@RequestParam String name) {
        List<Manager> managers = investorService.searchManagersByName(name);

        if (managers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No managers found with the given name.");
        }

        return ResponseEntity.ok(managers);
    }

    // Search managers by sport
    @GetMapping("/managers/searchBySport")
    public ResponseEntity<?> searchManagersBySport(@RequestParam(required = false) String sport) {
        if (sport == null || sport.isEmpty()) {
            return ResponseEntity.badRequest().body("Sport parameter is required.");
        }

        List<Manager> managers = investorService.searchManagersBySport(sport);

        if (managers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No managers found in the given sport.");
        }

        return ResponseEntity.ok(managers);
    }

    // Search managers by financial status
    @GetMapping("/managers/searchByFinancialStatus")
    public ResponseEntity<?> searchManagersByFinancialStatus(@RequestParam String financialStatus) {
        List<Manager> managers = investorService.searchManagersByFinancialStatus(financialStatus);

        if (managers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No managers found with the given financial status.");
        }

        return ResponseEntity.ok(managers);
    }



}
//@PostMapping("/confirm-investment")
//public ResponseEntity<Object> confirmInvestment(@RequestBody Investment investment)
//        throws MessagingException, UnsupportedEncodingException {
//
//    Investor investor = investment.getInvestor();
//
//    String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
//    String mailFromName = environment.getProperty("mail.from.name", "ArenaBoost");
//
//    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
//    final MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//
//    email.setTo(investor.getEmail());
//    email.setSubject(MAIL_SUBJECT);
//    email.setFrom(new InternetAddress(mailFrom, mailFromName));
//
//    // Convert `Date` to `LocalDateTime`
//    LocalDateTime createdAt = investor.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//
//    // Format the date
//    String formattedDate = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//
//
//    final Context ctx = new Context(LocaleContextHolder.getLocale());
//    ctx.setVariable("investorId", investor.getInvestorId());
//    ctx.setVariable("name", investor.getName());
//    ctx.setVariable("email", investor.getEmail());
//    ctx.setVariable("phone", investor.getPhone());
//    ctx.setVariable("investmentBudget", investor.getInvestment_budget());
//    ctx.setVariable("riskTolerance", investor.getRiskTolerance());
//    ctx.setVariable("createdAt", formattedDate);
//    ctx.setVariable("investmentId", investment.getInvestmentId());
//    ctx.setVariable("investmentType", investment.getAthlete() != null ? "Athlete" : "Project");
//    ctx.setVariable("amount", investment.getAmount());
//        ctx.setVariable("description", investment.getDescription());
//    ctx.setVariable("isActive", investment.isActive() ? "Yes" : "No");
//    ctx.setVariable("status", investment.getStatus());
//
//    ctx.setVariable("springLogo", SPRING_LOGO_IMAGE);
//
//    final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);
//    email.setText(htmlContent, true);
//
//    ClassPathResource clr = new ClassPathResource(SPRING_LOGO_IMAGE);
//    email.addInline("springLogo", clr, PNG_MIME);
//
//    mailSender.send(mimeMessage);
//
//    Map<String, String> body = new HashMap<>();
//    body.put("message", "Investment confirmation email sent successfully.");
//
//    return new ResponseEntity<>(body, HttpStatus.OK);
//}