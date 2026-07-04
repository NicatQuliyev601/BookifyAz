package com.bookifyaz.bookifyaz.email;

import com.bookifyaz.bookifyaz.entity.Booking;
import com.bookifyaz.bookifyaz.entity.Notification;
import com.bookifyaz.bookifyaz.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    public NotificationService(JavaMailSender mailSender, NotificationRepository notificationRepository) {
        this.mailSender = mailSender;
        this.notificationRepository = notificationRepository;
    }

    @Async
    public void sendBookingConfirmation(Booking booking, String businessName) {
        String clientEmail = booking.getClient().getEmail();
        if (clientEmail == null || clientEmail.isBlank()) {
            log.warn("[Notification] Client has no email, skipping. bookingId={}", booking.getId());
            saveRecord(booking, clientEmail, false);
            return;
        }

        String html = BookingEmailTemplate.build(
                booking.getClient().getFullName(),
                businessName,
                booking.getService().getName(),
                booking.getStaff().getUser().getFullName(),
                booking.getStartAt(),
                booking.getEndAt(),
                booking.getStatus(),
                booking.getId()
        );

        boolean sent = sendHtmlEmail(
                clientEmail,
                "Your booking is confirmed — " + businessName,
                html
        );

        saveRecord(booking, clientEmail, sent);
    }

    private boolean sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("[Notification] Email sent to {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("[Notification] Failed to send email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    private void saveRecord(Booking booking, String recipient, boolean sent) {
        Notification notification = new Notification();
        notification.setChannel("EMAIL");
        notification.setRecipient(recipient);
        notification.setType("BOOKING_CONFIRMATION");
        notification.setSent(sent);
        notification.setSentAt(Timestamp.from(Instant.now()));
        notification.setBooking(booking);
        notificationRepository.save(notification);
    }
}
