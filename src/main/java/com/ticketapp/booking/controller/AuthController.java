package com.ticketapp.booking.controller;


import com.ticketapp.booking.dto.AuthRequestDTO;
import com.ticketapp.booking.dto.AuthResponseDTO;
import com.ticketapp.booking.dto.UserRequestDTO;
import com.ticketapp.booking.entity.User;
import com.ticketapp.booking.service.AuthService;
import com.ticketapp.booking.utill.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<StandardResponse> register(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        String authResponse = authService.registerUser(userRequestDTO);
        return new ResponseEntity<>(
                new StandardResponse(201,"User registered successfully",authResponse)
                , HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.loginUser(request));
    }
}


