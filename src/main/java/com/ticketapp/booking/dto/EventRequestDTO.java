package com.ticketapp.booking.dto;


import com.ticketapp.booking.entity.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventRequestDTO {

    @NotBlank(message = "Tittle cannot be empty !")
    private String title;

    @NotBlank(message = "Location cannot be empty !")
    private String location;

    @NotNull(message = "Enter the ticket price !")
    @Positive(message = "Price must be a positive !")
    private double ticketPrice;

    @NotNull(message = "Enter the total ticket count !")
    @Min(value = 1,message = "At least should buy a one ticket !")
    private Integer totalTickets;

    private String imageUrl;

    private String cardImageUrl;

    @NotNull(message = "Enter the event date !")
    private LocalDateTime eventDate;

    @NotNull(message = "Select a category !")
    private Category category;

    private String subCategory;

}