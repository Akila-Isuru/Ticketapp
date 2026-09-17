package com.ticketapp.booking.controller;

import com.ticketapp.booking.dto.TicketTierRequestDTO;
import com.ticketapp.booking.dto.TicketTierResponseDTO;
import com.ticketapp.booking.service.TicketTierService;
import com.ticketapp.booking.utill.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events/{eventId}/tiers")
@RequiredArgsConstructor
@CrossOrigin
public class TicketTierController {

    private final TicketTierService ticketTierService;

    @PostMapping
    public ResponseEntity<StandardResponse> createTier(
            @PathVariable Long eventId,
            @RequestBody @Valid TicketTierRequestDTO dto) {
        TicketTierResponseDTO tier = ticketTierService.createTier(eventId, dto);
        return new ResponseEntity<>(
                new StandardResponse(201, "Ticket tier created successfully", tier),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<StandardResponse> getTiers(@PathVariable Long eventId) {
        List<TicketTierResponseDTO> tiers = ticketTierService.getTiersForEvent(eventId);
        return new ResponseEntity<>(
                new StandardResponse(200, "Ticket tiers fetched successfully", tiers),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{tierId}")
    public ResponseEntity<StandardResponse> deleteTier(
            @PathVariable Long eventId,
            @PathVariable Long tierId) {
        ticketTierService.deleteTier(tierId);
        return new ResponseEntity<>(
                new StandardResponse(200, "Ticket tier deleted successfully", null),
                HttpStatus.OK
        );
    }
}