package com.ticketapp.booking.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "ticket_tier_id")
    private TicketTier ticketTier;

    @Column(nullable = false)
    private Integer ticketCount;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String paymentStatus; //  PENDING, PAID, FAILED

    private String payherePaymentId;

    private LocalDateTime bookingTime;

    @PrePersist
    void onCreate(){
        this.bookingTime = LocalDateTime.now();
    }
}