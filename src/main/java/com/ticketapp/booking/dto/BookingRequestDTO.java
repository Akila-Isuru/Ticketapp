package com.ticketapp.booking.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingRequestDTO {

    @NotNull(message = "User Id is required !")
    private Long userId;

    @NotNull(message = "Event Id is required !")
    private Long eventId;

    @NotNull(message = "Ticket count is required !")
    @Min(value = 1,message = "At least should buy a 1 ticket")
    private Integer ticketCount;
}
