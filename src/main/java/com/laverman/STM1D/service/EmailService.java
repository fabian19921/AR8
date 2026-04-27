package com.laverman.STM1D.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendContactEmail(String senderName, String senderEmail,
                                 String subject, String message) {
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, false, "UTF-8");

            helper.setFrom("fabianlaverman1@gmail.com");
            helper.setTo("Fabianlaverman1992@live.nl");
            helper.setReplyTo(senderEmail);
            helper.setSubject("AR-8 Contact: " + subject);
            helper.setText(
                    "Bericht van: " + senderName + "\n" +
                            "Email: " + senderEmail + "\n\n" +
                            message
            );

            mailSender.send(mail);
        } catch (Exception e) {
            throw new RuntimeException("Email verzenden mislukt: " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(String toEmail, String name) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("fabianlaverman1@gmail.com");
        mail.setTo(toEmail);
        mail.setSubject("Welkom bij AR-8!");
        mail.setText(
                "Hallo " + name + ",\n\n" +
                        "Je account is succesvol aangemaakt bij AR-8.\n\n" +
                        "Je kunt nu inloggen op:\n" +
                        "http://localhost:8080\n\n" +
                        "Always Ready!\n\n" +
                        "Team AR-8"
        );
        mailSender.send(mail);
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("fabianlaverman1@gmail.com");
        mail.setTo(toEmail);
        mail.setSubject("AR-8 - Wachtwoord reset");
        mail.setText(
                "Hallo,\n\n" +
                        "Je hebt een wachtwoord reset aangevraagd voor je AR-8 account.\n\n" +
                        "Klik op de onderstaande link om je wachtwoord te wijzigen:\n" +
                        resetLink + "\n\n" +
                        "Deze link is 1 uur geldig.\n\n" +
                        "Als je dit niet hebt aangevraagd, kun je deze email negeren.\n\n" +
                        "Team AR-8"
        );
        mailSender.send(mail);
    }
}