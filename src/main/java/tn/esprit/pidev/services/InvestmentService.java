package tn.esprit.pidev.services;


import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.EmailDetails;
import tn.esprit.pidev.entities.Investment;
import tn.esprit.pidev.entities.Investor;
import tn.esprit.pidev.repository.InvestmentRepository;
import tn.esprit.pidev.repository.InvestorRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

@Service
public class InvestmentService implements IInvestmentService {

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
                    + "<li><strong>Amount:</strong> " + investment.getAmount() + "</li>"
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
}
