package com.example.project.projectbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceProject {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceProject(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendProjectLaunchEmail(String to, String projectName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("votre_email@gmail.com"); // Remplacez par votre adresse e-mail
        message.setTo(to);
        message.setSubject("Nouveau Projet Lancé : " + projectName);
        message.setText("Bonjour,\n\nUn nouveau projet, " + projectName + ", a été lancé sur notre plateforme de crowdfunding.\n" +
                "Consultez-le dès maintenant sur http://localhost:8089/project/retrieve-project/1\n\nCordialement,\nL'équipe Arena Boost");

        mailSender.send(message);
    }
}