package com.ticketapp.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthRequestDTO {
    private String email;
    private String password;
}
