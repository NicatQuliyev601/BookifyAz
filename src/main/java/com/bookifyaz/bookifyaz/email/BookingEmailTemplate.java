package com.bookifyaz.bookifyaz.email;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingEmailTemplate {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public static String build(
            String clientName,
            String businessName,
            String serviceName,
            String staffName,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String status,
            int bookingId
    ) {
        String date      = startAt.format(DATE_FMT);
        String startTime = startAt.format(TIME_FMT);
        String endTime   = endAt.format(TIME_FMT);
        String ref       = String.format("#%06d", bookingId);

        return "<!DOCTYPE html>" +
               "<html lang=\"en\"><head>" +
               "<meta charset=\"UTF-8\"/>" +
               "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\"/>" +
               "<title>Booking Confirmation</title></head>" +
               "<body style=\"margin:0;padding:0;background:#f0f4f8;font-family:'Segoe UI',Arial,sans-serif;\">" +

               "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#f0f4f8;padding:48px 16px;\">" +
               "<tr><td align=\"center\">" +
               "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;width:100%;\">" +

               // ── HEADER ──────────────────────────────────────────────────
               "<tr><td style=\"" +
               "background:linear-gradient(135deg,#0a0f1e 0%,#0f2a4a 55%,#0369a1 100%);" +
               "border-radius:20px 20px 0 0;padding:52px 40px 44px;text-align:center;\">" +

               "<div style=\"display:inline-block;width:72px;height:72px;line-height:72px;" +
               "border-radius:50%;border:2px solid rgba(255,255,255,0.25);" +
               "background:rgba(255,255,255,0.08);font-size:30px;margin-bottom:24px;\">✦</div>" +

               "<h1 style=\"margin:0 0 10px;color:#ffffff;font-size:30px;font-weight:700;" +
               "letter-spacing:-0.5px;\">Booking Confirmed</h1>" +

               "<p style=\"margin:0;color:#7dd3fc;font-size:15px;\">Your appointment is all set</p>" +

               "</td></tr>" +

               // ── BODY ────────────────────────────────────────────────────
               "<tr><td style=\"background:#ffffff;padding:44px 40px;\">" +

               // greeting
               "<p style=\"margin:0 0 32px;font-size:16px;color:#374151;line-height:1.7;\">" +
               "Hello, <strong style=\"color:#0f172a;\">" + clientName + "</strong> 👋<br/>" +
               "Your reservation at <strong style=\"color:#0369a1;\">" + businessName + "</strong> " +
               "is confirmed and waiting for you. Here are your appointment details:" +
               "</p>" +

               // detail table
               "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" " +
               "style=\"border-radius:14px;border:1px solid #e2e8f0;overflow:hidden;margin-bottom:32px;\">" +

               detailRow("🪒 Service",  serviceName,  true)  +
               detailRow("👤 Staff",    staffName,    false) +
               detailRow("📅 Date",     date,         true)  +
               detailRow("🕐 Time",     startTime + " – " + endTime, false) +
               statusRow(status) +

               "</table>" +

               // booking reference
               "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" " +
               "style=\"background:linear-gradient(135deg,#0a0f1e,#0f2a4a);border-radius:14px;margin-bottom:32px;\">" +
               "<tr><td style=\"padding:24px;text-align:center;\">" +
               "<p style=\"margin:0 0 6px;color:#7dd3fc;font-size:11px;font-weight:700;" +
               "text-transform:uppercase;letter-spacing:2px;\">Booking Reference</p>" +
               "<p style=\"margin:0;color:#ffffff;font-size:28px;font-weight:800;letter-spacing:6px;\">" + ref + "</p>" +
               "</td></tr></table>" +

               // reminder callout
               "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" " +
               "style=\"background:#fff7ed;border-left:4px solid #f97316;border-radius:0 10px 10px 0;margin-bottom:36px;\">" +
               "<tr><td style=\"padding:18px 20px;\">" +
               "<p style=\"margin:0;color:#92400e;font-size:14px;line-height:1.7;\">" +
               "⏰ <strong>Reminder:</strong> Please arrive 5 minutes before your appointment. " +
               "If you need to cancel or reschedule, please contact us as soon as possible." +
               "</p>" +
               "</td></tr></table>" +

               // closing
               "<p style=\"margin:0;font-size:15px;color:#64748b;line-height:1.8;text-align:center;\">" +
               "Thank you for choosing <strong style=\"color:#0369a1;\">" + businessName + "</strong>.<br/>" +
               "We look forward to seeing you! 🌟" +
               "</p>" +

               "</td></tr>" +

               // ── FOOTER ──────────────────────────────────────────────────
               "<tr><td style=\"background:#0a0f1e;border-radius:0 0 20px 20px;padding:28px 40px;text-align:center;\">" +
               "<p style=\"margin:0 0 8px;color:#64748b;font-size:13px;\">" +
               "This is an automated confirmation email. Please do not reply." +
               "</p>" +
               "<p style=\"margin:0;color:#334155;font-size:12px;\">" +
               "Powered by <strong style=\"color:#38bdf8;\">BookifyAZ</strong> &nbsp;·&nbsp; Smart Booking Platform" +
               "</p>" +
               "</td></tr>" +

               "</table>" +
               "</td></tr></table>" +
               "</body></html>";
    }

    private static String detailRow(String label, String value, boolean shaded) {
        String bg = shaded ? "background:#f8fafc;" : "background:#ffffff;";
        return "<tr style=\"border-bottom:1px solid #e2e8f0;\">" +
               "<td style=\"" + bg + "padding:15px 20px;width:38%;\">" +
               "<span style=\"font-size:12px;font-weight:700;color:#94a3b8;" +
               "text-transform:uppercase;letter-spacing:0.8px;\">" + label + "</span>" +
               "</td>" +
               "<td style=\"" + bg + "padding:15px 20px;\">" +
               "<span style=\"font-size:15px;font-weight:600;color:#0f172a;\">" + value + "</span>" +
               "</td></tr>";
    }

    private static String statusRow(String status) {
        return "<tr>" +
               "<td style=\"background:#f8fafc;padding:15px 20px;width:38%;\">" +
               "<span style=\"font-size:12px;font-weight:700;color:#94a3b8;" +
               "text-transform:uppercase;letter-spacing:0.8px;\">✅ Status</span>" +
               "</td>" +
               "<td style=\"background:#f8fafc;padding:15px 20px;\">" +
               "<span style=\"display:inline-block;background:#dcfce7;color:#15803d;" +
               "font-size:13px;font-weight:700;padding:5px 14px;border-radius:20px;\">" +
               status + "</span>" +
               "</td></tr>";
    }
}
