package com.ticketapp.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketTierResponseDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer totalCount;
    private Integer availableCount;
}