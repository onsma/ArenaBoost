package tn.esprit.pidev.utils;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.stripe.model.checkout.Session;
import tn.esprit.pidev.entities.Insurance;
import tn.esprit.pidev.services.PdfService;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmailServiceInsurance {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PdfService pdfService;

    @Async
    public void sendInsuranceEmail(String to, Insurance insurance) {
        // Generate PDF as byte array from insurance details
        byte[] pdfBytes = pdfService.generateInsurancePdfWithBackgroundAndLogo(insurance);

        // Generate the HTML content (could still include a checkout URL, etc.)
        String htmlContent = generateHtmlContent(insurance);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            // Set multipart=true to allow attachments
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Your Insurance Policy Details");

            // Set the HTML content
            helper.setText(htmlContent, true);

            // Attach the PDF file as an inline attachment
            // Here, "policyPdf" is the content ID that could be used in the HTML if needed.
            helper.addAttachment("InsurancePolicy.pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);
            System.out.println("Insurance email with PDF sent to " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateHtmlContent(Insurance insurance) {
        String userName = (insurance.getUser() != null && insurance.getUser().getFirstName() != null)
                ? insurance.getUser().getFirstName() : "Customer";
        // For demonstration, we'll assume the checkoutUrl is generated elsewhere.
        String checkoutUrl = "#";
        try {
            checkoutUrl = generateCheckoutUrlForInsurance(insurance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<title>Your Insurance Policy Details</title>");
        html.append("<style>");
        html.append("body { background-color: #f4f4f4; margin: 0; padding: 0; font-family: Arial, sans-serif; }");
        html.append(".email-container { background-color: #ffffff; max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #dddddd; border-radius: 5px; }");
        html.append(".header { background-color: #0056b3; color: #ffffff; padding: 10px; text-align: center; font-size: 24px; font-weight: bold; }");
        html.append(".content { margin: 20px 0; line-height: 1.6; color: #333333; }");
        html.append(".footer { text-align: center; font-size: 12px; color: #777777; }");
        html.append("a.button { display: inline-block; padding: 10px 20px; background-color: #28a745; color: #ffffff; text-decoration: none; border-radius: 5px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"email-container\">");
        html.append("<div class=\"header\">Insurance Policy Confirmation</div>");
        html.append("<div class=\"content\">");
        html.append("<p>Dear " + userName + ",</p>");
        html.append("<p>Thank you for subscribing to our insurance policy. Please find attached your detailed policy document in PDF format.</p>");
        html.append("<p>To complete your payment, please click the link below:</p>");
        html.append("<p><a class=\"button\" href='" + checkoutUrl + "'>Pay Now</a></p>");
        html.append("<p>Please keep this email for your records. If you have any questions, feel free to contact our support team.</p>");
        html.append("<p>Best regards,<br>Insurance Team</p>");
        html.append("</div>");
        html.append("<div class=\"footer\">&copy; 2025 Your Company Name. All rights reserved.</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }

    /**
     * Generates a Stripe Checkout Session URL for the given Insurance.
     * (For brevity, implementation is similar to previous examples.)
     */
    private String generateCheckoutUrlForInsurance(Insurance insurance) throws StripeException {
        // Set the Stripe API key
        Stripe.apiKey = stripeApiKey;

        // Convert the insurance amount to cents (assumes amount is in dollars)
        long amountInCents = Math.round(insurance.getAmount() * 100);

        // Build line items for the checkout session
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        lineItems.add(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")  // Adjust currency if needed
                                        .setUnitAmount(amountInCents)
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Insurance Payment for Insurance ID: " + insurance.getId_insurance())
                                                        .build()
                                        )
                                        .build()
                        )
                        .build()
        );

        // Build session parameters with success and cancel URLs
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://example.com/success")  // Replace with your actual URL
                .setCancelUrl("https://example.com/cancel")    // Replace with your actual URL
                .addAllLineItem(lineItems)
                .build();

        // Create the Stripe Checkout Session
        Session session = Session.create(params);
        return session.getUrl();
    }
}