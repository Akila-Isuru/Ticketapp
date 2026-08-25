package com.ticketapp.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDTO {
    @NotBlank(message = "Name cannot be Empty!")
    private String name;
    @NotBlank(message = "Email cannot be Empty!")
    @Email(message = "Invalid email format!")
    private String email;
    private String phone;

}
