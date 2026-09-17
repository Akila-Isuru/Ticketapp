package com.ticketapp.booking.service;

import com.ticketapp.booking.dto.TicketTierRequestDTO;
import com.ticketapp.booking.dto.TicketTierResponseDTO;
import com.ticketapp.booking.entity.Event;
import com.ticketapp.booking.entity.TicketTier;
import com.ticketapp.booking.exception.NotFoundException;
import com.ticketapp.booking.repo.EventRepository;
import com.ticketapp.booking.repo.TicketTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketTierService {

    private final TicketTierRepository ticketTierRepo;
    private final EventRepository eventRepo;

    public TicketTierResponseDTO createTier(Long eventId, TicketTierRequestDTO dto) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        TicketTier tier = new TicketTier();
        tier.setName(dto.getName());
        tier.setPrice(dto.getPrice());
        tier.setTotalCount(dto.getTotalCount());
        tier.setAvailableCount(dto.getTotalCount());
        tier.setEvent(event);

        ticketTierRepo.save(tier);
        return mapToDTO(tier);
    }

    public List<TicketTierResponseDTO> getTiersForEvent(Long eventId) {
        return ticketTierRepo.findByEventId(eventId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public void deleteTier(Long tierId) {
        TicketTier tier = ticketTierRepo.findById(tierId)
                .orElseThrow(() -> new NotFoundException("Ticket tier not found"));
        ticketTierRepo.delete(tier);
    }

    private TicketTierResponseDTO mapToDTO(TicketTier tier) {
        return new TicketTierResponseDTO(
                tier.getId(),
                tier.getName(),
                tier.getPrice(),
                tier.getTotalCount(),
                tier.getAvailableCount()
        );
    }
}