package com.ticketapp.booking.service;

import com.ticketapp.booking.utill.QRCodeGenerator;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendBookingConfirmationEmail(String toEmail,
                                             String eventTitle,
                                             String orderId,
                                             Integer ticketCount,
                                             Double totalAmount
    ) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8"
            );

            helper.setFrom("TuneTix Team <" + getFromAddress() + ">");
            helper.setTo(toEmail);
            helper.setSubject("Your ticket is confirmed — " + eventTitle);

            String htmlContent = buildEmailHtml(eventTitle, orderId, ticketCount, totalAmount);
            helper.setText(htmlContent, true);

            byte[] qrCodeImage = QRCodeGenerator.generateQRCodeImage(orderId, 220, 220);
            helper.addInline("qrCodeImage", new ByteArrayResource(qrCodeImage), "image/png");

            mailSender.send(message);

        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    private String getFromAddress() {
        return ((org.springframework.mail.javamail.JavaMailSenderImpl) mailSender).getUsername();
    }


    private String buildEmailHtml(String eventTitle, String orderId, Integer ticketCount, Double totalAmount) {
        return "<div style='font-family: Arial, sans-serif; max-width: 480px; margin: 0 auto; color: #1e293b;'>"
                + "<div style='background-color: #4f46e5; padding: 20px; border-radius: 12px 12px 0 0; text-align: center;'>"
                + "<h1 style='color: #ffffff; margin: 0; font-size: 20px;'> TuneTix</h1>"
                + "</div>"

                + "<div style='border: 1px solid #e5e7eb; border-top: none; border-radius: 0 0 12px 12px; padding: 24px;'>"
                + "<p style='font-size: 15px;'>Hi there,</p>"
                + "<p style='font-size: 15px;'>Your payment was successful and your ticket is confirmed. Here are your booking details:</p>"

                + "<table style='width: 100%; border-collapse: collapse; margin: 16px 0; font-size: 14px;'>"
                + "<tr><td style='padding: 6px 0; color: #64748b;'>Event</td><td style='padding: 6px 0; text-align: right; font-weight: bold;'>" + eventTitle + "</td></tr>"
                + "<tr><td style='padding: 6px 0; color: #64748b;'>Order ID</td><td style='padding: 6px 0; text-align: right;'>" + orderId + "</td></tr>"
                + "<tr><td style='padding: 6px 0; color: #64748b;'>Tickets</td><td style='padding: 6px 0; text-align: right;'>" + ticketCount + "</td></tr>"
                + "<tr><td style='padding: 6px 0; color: #64748b;'>Total Paid</td><td style='padding: 6px 0; text-align: right; color: #16a34a; font-weight: bold;'>LKR " + totalAmount + "</td></tr>"
                + "</table>"

                + "<div style='text-align: center; margin: 24px 0;'>"
                + "<p style='font-size: 13px; color: #64748b; margin-bottom: 8px;'>Show this QR code at the entrance</p>"
                + "<img src='cid:qrCodeImage' width='180' height='180' style='border: 1px solid #e5e7eb; border-radius: 8px; padding: 8px;' />"
                + "</div>"

                + "<p style='font-size: 13px; color: #94a3b8; text-align: center; margin-top: 24px;'>Thank you for booking with TuneTix.</p>"
                + "</div>"
                + "</div>";
    }

}