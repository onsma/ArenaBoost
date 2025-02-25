package tn.esprit.pidev.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Loan;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void envoyerNotificationLoan(Loan loan) {
        String destinataire = loan.getUser().getEmail();  // Email du demandeur du prêt
        String sujet = "Mise à jour de votre demande de prêt - ArenaBoost";
        String logoUrl = "http://www.image-heberg.fr/files/17404892802444178759.png"; // URL du logo

        // 🖋️ **HTML du message**
        String message = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px;'>"
                + "<div style='text-align: center;'>"
                + "<img src='" + logoUrl + "' width='150' alt='ArenaBoost Logo' style='margin-bottom: 20px;'/>"
                + "</div>"
                + "<p>Bonjour <strong>" + loan.getUser().getFirstName() + "</strong>,</p>"
                + "<p>Votre demande de prêt d'un montant de <strong>" + loan.getAmount() + " TND</strong> a été mise à jour.</p>"
                + "<p><strong>Statut actuel :</strong> " + loan.getStatus() + "</p>"
                + "<p>Merci d'utiliser <strong>ArenaBoost</strong>.</p>"
                + "<p style='margin-top: 20px;'>Cordialement,<br><strong>L'équipe ArenaBoost</strong></p>"
                + "</div>";

        try {
            MimeMessage email = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(email, true, "UTF-8");
            helper.setTo(destinataire);
            helper.setSubject(sujet);
            helper.setText(message, true); // 🔥 **Active l'affichage HTML**
            helper.setFrom("skandernacheb@gmail.com");

            mailSender.send(email);
            System.out.println("📩 Email envoyé avec succès !");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de l'envoi de l'email.");
        }
    }
}
