//package tn.esprit.pidev.utils;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import tn.esprit.pidev.entities.Insurance;
//
//@Service
//public class EmailServiceInsurance {
//
//    @Autowired
//    private JavaMailSender mailSender;
//    @Autowired
//    public EmailServiceInsurance(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//    /**
//     * Asynchronously sends an email with the Insurance details
//     * to the specified recipient (the user's email).
//     */
//    @Async
//    public void sendInsuranceEmail(String to, Insurance insurance) {
//        // 1. Generate the HTML content
//        String htmlContent = generateHtmlContent(insurance);
//
//        // 2. Create the MimeMessage
//        MimeMessage message = mailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(to);
//            helper.setSubject("Your Insurance Details");
//            helper.setText(htmlContent, true); // 'true' indicates HTML content
//            mailSender.send(message);
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            // You could log or re-throw as a custom exception if desired
//        }
//    }
//
//    private String generateHtmlContent(Insurance insurance) {
//        String userName = (insurance.getUser() != null && insurance.getUser().getFirstName() != null)
//                ? insurance.getUser().getFirstName() : "Customer";
//        StringBuilder html = new StringBuilder();
//        html.append("<!DOCTYPE html>");
//        html.append("<html lang=\"en\">");
//        html.append("<head>");
//        html.append("<meta charset=\"UTF-8\">");
//        html.append("<title>Your Insurance Policy Details</title>");
//        // Inline CSS for email compatibility
//        html.append("<style>");
//        html.append("body { background-color: #f4f4f4; margin: 0; padding: 0; font-family: Arial, sans-serif; }");
//        html.append(".email-container { background-color: #ffffff; max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #dddddd; border-radius: 5px; }");
//        html.append(".header { background-color: #0056b3; color: #ffffff; padding: 10px; text-align: center; font-size: 24px; font-weight: bold; }");
//        html.append(".content { margin: 20px 0; line-height: 1.6; color: #333333; }");
//        html.append(".footer { text-align: center; font-size: 12px; color: #777777; }");
//        html.append("</style>");
//        html.append("</head>");
//        html.append("<body>");
//        html.append("<div class=\"email-container\">");
//        html.append("<div class=\"header\">Insurance Policy Confirmation</div>");
//        html.append("<div class=\"content\">");
//        html.append("<p>Dear " + userName + ",</p>");
//        html.append("<p>Thank you for subscribing to our insurance policy. Here are the details of your policy:</p>");
//        html.append("<ul>");
//        html.append("<li><strong>Policy Type:</strong> " + insurance.getTypeInsurance() + "</li>");
//        html.append("<li><strong>Coverage Amount:</strong> " + insurance.getAmount() + "</li>");
//        html.append("<li><strong>Subscription Date:</strong> " + insurance.getSubscription_date() + "</li>");
//        html.append("<li><strong>Renewal Date:</strong> " + insurance.getRenewal_date() + "</li>");
//        html.append("<li><strong>Status:</strong> " + insurance.getStatusInsurance() + "</li>");
//        html.append("</ul>");
//        html.append("<p>Please keep this email for your records. If you have any questions, feel free to contact our support team.</p>");
//        html.append("<p>Best regards,<br>Insurance Team</p>");
//        html.append("</div>");
//        html.append("<div class=\"footer\">&copy; 2025 Your Company Name. All rights reserved.</div>");
//        html.append("</div>");
//        html.append("</body>");
//        html.append("</html>");
//
//        return html.toString();
//    }
//
//}
//
package tn.esprit.pidev.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Insurance;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class EmailServiceInsurance {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendInsuranceEmail(String to, Insurance insurance) {
        // 1. Generate the HTML content
        String htmlContent = generateHtmlContent(insurance);

        // 2. Create the MimeMessage and send email
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Your Insurance Policy Details");
            helper.setText(htmlContent, true); // true for HTML content
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Log or rethrow as needed
        }
    }

    /**
     * Generates the HTML content for the insurance email, including a link to the Stripe Checkout page.
     */
    private String generateHtmlContent(Insurance insurance) {
        String userName = (insurance.getUser() != null && insurance.getUser().getFirstName() != null)
                ? insurance.getUser().getFirstName() : "Customer";
        String checkoutUrl = "#";
        try {
            // Generate the Stripe Checkout session URL for the insurance payment
            checkoutUrl = generateCheckoutUrlForInsurance(insurance);
        } catch (StripeException e) {
            e.printStackTrace();
        }

        // Read the background image from resources and encode it to Base64

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<title>Your Insurance Policy Details</title>");
        html.append("<style>");
        // Semi-transparent container so background shows through
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
        html.append("<p>Thank you for subscribing to our insurance policy. Here are the details of your policy:</p>");
        html.append("<ul>");
        html.append("<li><strong>Policy Type:</strong> " + insurance.getTypeInsurance() + "</li>");
        html.append("<li><strong>Coverage Amount:</strong> " + insurance.getAmount() + "</li>");
        html.append("<li><strong>Subscription Date:</strong> " + insurance.getSubscription_date() + "</li>");
        html.append("<li><strong>Renewal Date:</strong> " + insurance.getRenewal_date() + "</li>");
        html.append("<li><strong>Status:</strong> " + insurance.getStatusInsurance() + "</li>");
        html.append("</ul>");
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
     * The amount is converted to the smallest currency unit (e.g., cents for USD).
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
