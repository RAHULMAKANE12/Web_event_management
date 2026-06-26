package com.spoorthy.ems.service;

import com.spoorthy.ems.entity.Event;
import com.spoorthy.ems.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendRegistrationConfirmation(User user, Event event) {
        if (mailSender == null) {
            System.out.println("Mail sender not configured. Skipping email for: " + user.getEmail());
            System.out.println("Registration confirmation for event: " + event.getEventName());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Registration Confirmed - " + event.getEventName());
            helper.setText(buildEmailTemplate(user, event), true);
            mailSender.send(message);
            System.out.println("Confirmation email sent to: " + user.getEmail());
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    private String buildEmailTemplate(User user, Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Inter,Arial,sans-serif;padding:20px;'>");
        sb.append("<div style='max-width:600px;margin:0 auto;background:#f8fafc;border-radius:12px;padding:30px;'>");
        sb.append("<h1 style='color:#4f46e5;'>Registration Confirmed! ✅</h1>");
        sb.append("<p>Hi <strong>").append(user.getName()).append("</strong>,</p>");
        sb.append("<p>You have successfully registered for:</p>");
        sb.append("<div style='background:white;border-radius:8px;padding:20px;margin:20px 0;border-left:4px solid #4f46e5;'>");
        sb.append("<h2 style='color:#1e293b;margin:0 0 10px;'>").append(event.getEventName()).append("</h2>");
        sb.append("<p>📅 Date: <strong>").append(event.getEventDate()).append("</strong></p>");
        sb.append("<p>⏰ Time: <strong>").append(event.getEventTime()).append("</strong></p>");
        sb.append("<p>📍 Location: <strong>").append(event.getLocation()).append("</strong></p>");
        sb.append("<p>🏛️ Club: <strong>").append(event.getClub().getClubName()).append("</strong></p>");
        if (event.getGuestSpeaker() != null && !event.getGuestSpeaker().isBlank()) {
            sb.append("<p>🎤 Guest Speaker: <strong>").append(event.getGuestSpeaker()).append("</strong></p>");
        }
        sb.append("</div>");
        sb.append("<p>We look forward to seeing you there!</p>");
        sb.append("<p style='color:#94a3b8;font-size:12px;margin-top:30px;'>— Spoorthy Engineering College EMS</p>");
        sb.append("</div></body></html>");
        return sb.toString();
    }
}
