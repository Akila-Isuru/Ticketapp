package com.ticketapp.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g "Gold", "Platinum", "Balcony Front"

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer totalCount;

    @Column(nullable = false)
    private Integer availableCount;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}