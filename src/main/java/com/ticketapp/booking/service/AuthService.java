package com.ticketapp.booking.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.ticketapp.booking.dto.AuthRequestDTO;
import com.ticketapp.booking.dto.AuthResponseDTO;
import com.ticketapp.booking.dto.UserRequestDTO;
import com.ticketapp.booking.entity.Role;
import com.ticketapp.booking.entity.User;
import com.ticketapp.booking.repo.UserRepository;
import com.ticketapp.booking.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Value("${google.client.id}")
    private String googleClientId;

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

        User user = userRepo.findByEmail(authRequestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponseDTO(token);
    }

//    Google login

    public AuthResponseDTO loginWithGoogle(String idToken){
        try{
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken token = verifier.verify(idToken);
            if(token == null){
                throw new RuntimeException("Invalid token");
            }

            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            String name =(String) payload.get("name");

            User user = userRepo.findByEmail(email).orElseGet(()->{
                User newUser = new User();
                newUser.setName(name != null ? name : "Google User");
                newUser.setEmail(email);
                newUser.setRole(Role.ROLE_USER);
                return userRepo.save(newUser);
            });


            String jwt = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
            return new AuthResponseDTO(jwt);


    }catch (Exception e){
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }

}