package com.ticketapp.booking.service;

import com.ticketapp.booking.dto.EventRequestDTO;
import com.ticketapp.booking.dto.EventResponseDTO;
import com.ticketapp.booking.entity.Event;
import com.ticketapp.booking.exception.DuplicateException;
import com.ticketapp.booking.exception.NotFoundException;
import com.ticketapp.booking.repo.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public List<EventResponseDTO> searchEvents(String word){

        List<Event> events = eventRepo.findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(word,word);
        return modelMapper.map(events,new TypeToken<List<EventResponseDTO>>() {}.getType());
    }

    public Page<EventResponseDTO>getPagedEvents(int page,int size,String sortBy,String sortDir){

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page,size,sort);
        Page <Event> eventPage =eventRepo.findAll(pageable);

        return eventPage.map(event -> modelMapper.map(event, EventResponseDTO.class));
    }

    public EventResponseDTO updateEvent(Long eventId, EventRequestDTO eventRequestDTO) {
        Event event = eventRepo.findById(eventId).
                orElseThrow(()->new NotFoundException("Event not found"));

        int soldTickets = event.getTotalTickets() - event.getAvailableTickets();

        event.setTitle(eventRequestDTO.getTitle());
        event.setLocation(eventRequestDTO.getLocation());
        event.setTicketPrice(eventRequestDTO.getTicketPrice());
        event.setTotalTickets(eventRequestDTO.getTotalTickets());
        event.setImageUrl(eventRequestDTO.getImageUrl());
        event.setEventDate(eventRequestDTO.getEventDate());
        event.setAvailableTickets(eventRequestDTO.getTotalTickets() - soldTickets);

        eventRepo.save(event);
        return modelMapper.map(event,EventResponseDTO.class);

    }

    public void deleteEvent(Long eventId) {
        Event event = eventRepo.findById(eventId).
                orElseThrow(()->new NotFoundException("Event not found"));
        eventRepo.delete(event);

    }
}
