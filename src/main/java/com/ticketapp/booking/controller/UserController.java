package com.ticketapp.booking.controller;

import com.ticketapp.booking.dto.UserRequestDTO;
import com.ticketapp.booking.dto.UserResponseDTO;
import com.ticketapp.booking.entity.User;
import com.ticketapp.booking.service.UserService;
import com.ticketapp.booking.utill.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;
    @PostMapping
    public ResponseEntity<StandardResponse> createUser(@RequestBody @Valid UserRequestDTO userRequestDTO){
        UserResponseDTO newUser = userService.createUser(userRequestDTO);
        return new ResponseEntity<>(
                new StandardResponse(201,"User registered successfully",newUser),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<StandardResponse> getAllUser(){
        List<UserResponseDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(
                new StandardResponse(200, "Successfully fetched all users", users),
                HttpStatus.OK
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<StandardResponse> getUserById(@PathVariable Long userId){
        UserResponseDTO user = userService.getUserById(userId);
        return new ResponseEntity<>(
                new StandardResponse(200, "User found successfully", user),
                HttpStatus.OK
        );
    }
}
