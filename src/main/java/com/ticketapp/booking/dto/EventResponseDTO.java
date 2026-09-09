package com.ticketapp.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventResponseDTO {

    private Long id;
    private String title;
    private String location;
    private String ticketPrice;
    private Integer totalTickets;
    private Integer availableTickets;
    private String imageUrl;

}
