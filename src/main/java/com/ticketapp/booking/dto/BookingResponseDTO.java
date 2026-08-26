package com.ticketapp.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingResponseDTO {
    private Long bookingId;
    private String orderId;
    private String merchantId;
    private String eventTitle;
    private Integer ticketCount;
    private Double totalAmount;
    private String currency;
    private String hash;
    private String paymentStatus;     // PENDING / PAID
    private LocalDateTime bookingTime;

}
