package com.ticketapp.booking.service;

import com.ticketapp.booking.dto.BookingRequestDTO;
import com.ticketapp.booking.utill.QRCodeGenerator;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
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

           MimeMessageHelper helper = new MimeMessageHelper(message,true);
           helper.setTo(toEmail);
           helper.setSubject("Booking Confirmation & Ticket - " + eventTitle);


           String content = "Hello,\n\n" +
                   "Your payment was successful! Here are your booking details:\n\n" +
                   "Order ID: " + orderId + "\n" +
                   "Event: " + eventTitle + "\n" +
                   "Tickets: " + ticketCount + "\n" +
                   "Total Paid: LKR " + totalAmount + "\n\n" +
                   "Thank you for booking with us!";

           helper.setText(content);

           byte [] qrCodeImage = QRCodeGenerator.generateQRCodeImage(orderId,250,250);
           helper.addAttachment("QRCode_Ticket.png",new ByteArrayResource(qrCodeImage));
           mailSender.send(message);

       }catch (Exception e){
           System.out.println("Error : " + e.getMessage());
       }


    }

}
