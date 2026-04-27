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
            helper.setSubject("STM1D Contact: " + subject);
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
        mail.setSubject("Welkom bij STM1D!");
        mail.setText(
                "Hallo " + name + ",\n\n" +
                        "Je account is succesvol aangemaakt bij STM1D.\n\n" +
                        "Je kunt nu inloggen op:\n" +
                        "http://localhost:8080\n\n" +
                        "Veel plezier met trainen!\n\n" +
                        "Team STM1D"
        );
        mailSender.send(mail);
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("fabianlaverman1@gmail.com");
        mail.setTo(toEmail);
        mail.setSubject("STM1D - Wachtwoord reset");
        mail.setText(
                "Hallo,\n\n" +
                        "Je hebt een wachtwoord reset aangevraagd.\n\n" +
                        "Klik op de onderstaande link om je wachtwoord te wijzigen:\n" +
                        resetLink + "\n\n" +
                        "Deze link is 1 uur geldig.\n\n" +
                        "Als je dit niet hebt aangevraagd, kun je deze email negeren.\n\n" +
                        "STM1D"
        );
        mailSender.send(mail);
    }
}