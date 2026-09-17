package com.ticketapp.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketTierRequestDTO {

    @NotBlank(message = "Tier name is required !")
    private String name;

    @NotNull(message = "Enter the tier price !")
    @Positive(message = "Price must be positive !")
    private Double price;

    @NotNull(message = "Enter the total ticket count for this tier !")
    @Min(value = 1, message = "At least 1 ticket required !")
    private Integer totalCount;
}