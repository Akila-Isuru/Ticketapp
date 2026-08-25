package com.ticketapp.booking.service;

import com.ticketapp.booking.dto.EventRequestDTO;
import com.ticketapp.booking.dto.EventResponseDTO;
import com.ticketapp.booking.entity.Event;
import com.ticketapp.booking.exception.DuplicateException;
import com.ticketapp.booking.exception.NotFoundException;
import com.ticketapp.booking.repo.EventRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepo;
    private final ModelMapper modelMapper;

    public EventResponseDTO createEvent(EventRequestDTO eventRequestDTO) {
        boolean tittleExists = eventRepo.existsByTitle(eventRequestDTO.getTitle());
        if (tittleExists) {
            throw new DuplicateException("Title already exists");
        }
        Event event = modelMapper.map(eventRequestDTO, Event.class);
        event.setAvailableTickets(eventRequestDTO.getTotalTickets());
        eventRepo.save(event);
        return  modelMapper.map(event,EventResponseDTO.class);
    }
    public List<EventResponseDTO> getAllEvents(){
        List<Event> events = eventRepo.findAll();
        return modelMapper.map(events,new TypeToken<List<EventResponseDTO>>() {}.getType());
    }

    public EventResponseDTO getEventById(Long eventId){
        Event event = eventRepo.findById(eventId).orElseThrow(()->new NotFoundException("Event not found"));

        return modelMapper.map(event,EventResponseDTO.class);
    }
}
