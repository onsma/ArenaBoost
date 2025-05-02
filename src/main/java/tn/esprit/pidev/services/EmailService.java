package tn.esprit.pidev.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    // Send email method
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your_email@gmail.com");  // Your email address
        message.setTo(to);  // Receiver's email address
        message.setSubject(subject);  // Subject of the email
        message.setText(body);  // Body of the email
        javaMailSender.send(message);
    }
}



