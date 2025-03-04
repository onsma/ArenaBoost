package tn.esprit.pidev.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Investment;
import tn.esprit.pidev.entities.InvestmentStatus;
import tn.esprit.pidev.entities.InvestmentType;
import tn.esprit.pidev.entities.DividendPaymentFrequency;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import tn.esprit.pidev.entities.*;
import tn.esprit.pidev.repository.InvestmentRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.util.List;

@Service
public class InvestmentService implements IInvestmentService {

    private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);

    @Autowired
    InvestmentRepository investmentRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public Investment saveInvestment(Investment investment) {
        return investmentRepository.save(investment);
    }

    @Override
    public Investment findInvestmentById(long id) {
        return investmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Investment not found with ID:" + id));
    }

    @Override
    public List<Investment> findAllInvestments() {
        return investmentRepository.findAll();
    }


    @Override
    public void deleteInvestment(long id) {
        investmentRepository.deleteById(id);
    }

    @Override
    public Investment updateInvestment(long id, Investment investment) {
        Investment existingInvestment = findInvestmentById(id);
        existingInvestment.setAmount(investment.getAmount());
        existingInvestment.setStartDate(investment.getStartDate());
        existingInvestment.setEndDate(investment.getEndDate());
        existingInvestment.setInvestmentType(investment.getInvestmentType());
        existingInvestment.setRoiPercentage(investment.getRoiPercentage());
        existingInvestment.setStatus(investment.getStatus());
        return investmentRepository.save(investment);
    }

    @Override
    public String sendMail(EmailDetails details, Investment investment, Investor investor) {
        try {
            // Create the email body with investment and investor details
            String emailBody = "<html><body>"
                    + "<p>Dear " + investor.getName() + ",</p>"
                    + "<p>Thank you for your investment. Below are the details:</p>"
                    + "<ul>"
                    + "<li><strong>Your Budget:</strong> " + investor.getInvestment_budget() + "</li>"
                    + "<li><strong>Your Preferred Sport:</strong> " + investor.getPreferredSport() + "</li>"
                    + "<li><strong>Your Risk Tolerance:</strong> " + investor.getRiskTolerance() + "</li>"
                    + "<li><strong>Investment ID:</strong> " + investment.getInvestmentId() + "</li>"
                    + "<li><strong>Amount:</strong> " + investment.getAmount() + "TND</li>"
                    + "<li><strong>Created At:</strong> " + investment.getCreatedAt() + "</li>"
                    + "</ul>"
                    + "<p>Best regards,<br>Your Investment Team</p>"
                    + "<p><img src='cid:logo' alt='Startup Logo'></p>" // Embed logo here
                    + "</body></html>";

            // Create a MimeMessage
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // true indicates multipart message

            // Set email details
            helper.setFrom(sender);
            helper.setTo(investor.getEmail());
            helper.setSubject("Investment Confirmation");
            helper.setText(emailBody, true); // true indicates HTML content

            // Add the logo as an inline attachment
            Resource logoResource = new ClassPathResource("templates/images/arenaboost.png");
            helper.addInline("logo", logoResource); // "logo" matches the cid in the HTML

            // Send the email
            javaMailSender.send(mimeMessage);
            return "Mail Sent Successfully...";
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return "Error while Sending Mail";
        }
    }

    @Override
    public Investment calculateAndUpdateROI(long investmentId, double netProfit) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new RuntimeException("Investment not found"));
        double roi = (netProfit / investment.getAmount()) * 100; // Example ROI calculation
        investment.setRoiPercentage(roi);
        return investmentRepository.save(investment);
    }

    @Override
    public Double getROIForInvestment(long investmentId) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new RuntimeException("Investment not found"));
        return investment.getRoiPercentage();
    }

    @Override
    public List<Investment> getInvestmentWithROIForInvestor(long investorId) {
        return investmentRepository.findByInvestor_Id(investorId);
    }
    @Override
    public List<Investment> getInvestmentsByInvestorId(Long investorId){
        return investmentRepository.findByInvestor_Id(investorId);
    }

    @Override
    public List<Investment> findInvestmentsByCriteria(
            InvestmentType investmentType,
            Double minROI,
            Double maxROI,
            InvestmentStatus status,
            Double minAmount,
            Double maxAmount,
            DividendPaymentFrequency dividendPaymentFrequency,
            Double minDividendRate,
            Double maxDividendRate
    ) {
        logger.info("Filtering investments with criteria: investmentType={}, minROI={}, maxROI={}, status={}, minAmount={}, maxAmount={}, dividendPaymentFrequency={}, minDividendRate={}, maxDividendRate={}",
                investmentType, minROI, maxROI, status, minAmount, maxAmount, dividendPaymentFrequency, minDividendRate, maxDividendRate);

        Specification<Investment> spec = Specification.where(null);

        if (investmentType != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("investmentType"), investmentType));
            logger.info("Applied filter: investmentType={}", investmentType);
        }

        if (minROI != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("expectedROI"), minROI));
            logger.info("Applied filter: minROI={}", minROI);
        }

        if (maxROI != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("expectedROI"), maxROI));
            logger.info("Applied filter: maxROI={}", maxROI);
        }

        if (status != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
            logger.info("Applied filter: status={}", status);
        }

        if (minAmount != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
            logger.info("Applied filter: minAmount={}", minAmount);
        }

        if (maxAmount != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount));
            logger.info("Applied filter: maxAmount={}", maxAmount);
        }

        if (dividendPaymentFrequency != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("dividendPaymentFrequency"), dividendPaymentFrequency));
            logger.info("Applied filter: dividendPaymentFrequency={}", dividendPaymentFrequency);
        }

        if (minDividendRate != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("dividendRate"), minDividendRate));
            logger.info("Applied filter: minDividendRate={}", minDividendRate);
        }

        if (maxDividendRate != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("dividendRate"), maxDividendRate));
            logger.info("Applied filter: maxDividendRate={}", maxDividendRate);
        }

        List<Investment> result = investmentRepository.findAll(spec);
        logger.info("Found {} investments matching criteria", result.size());
        return result;
    }


    @Override
    public List<Investment> findInvestmentsByInvestorId(Long investorId) {
        return investmentRepository.findByInvestor_Id(investorId); // Fetch investments by investor ID
    }
}
