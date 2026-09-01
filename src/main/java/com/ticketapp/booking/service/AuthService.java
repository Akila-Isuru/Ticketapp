package com.ticketapp.booking.service;

import com.ticketapp.booking.dto.AuthRequestDTO;
import com.ticketapp.booking.dto.AuthResponseDTO;
import com.ticketapp.booking.dto.UserRequestDTO;
import com.ticketapp.booking.entity.Role;
import com.ticketapp.booking.entity.User;
import com.ticketapp.booking.repo.UserRepository;
import com.ticketapp.booking.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public String registerUser(UserRequestDTO dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("User with email already exists");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole() != null ? dto.getRole() : Role.ROLE_USER);

        userRepo.save(user);
        return "User registered successfully";
    }

    public AuthResponseDTO loginUser(AuthRequestDTO authRequestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getEmail(),
                        authRequestDTO.getPassword()
                )
        );
        String token = jwtUtils.generateToken(authRequestDTO.getEmail());
        return new AuthResponseDTO(token);
    }

}
