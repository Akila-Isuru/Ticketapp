package com.ticketapp.booking.controller;

import com.ticketapp.booking.dto.EventRequestDTO;
import com.ticketapp.booking.dto.EventResponseDTO;
import com.ticketapp.booking.service.EventService;
import com.ticketapp.booking.utill.StandardResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@CrossOrigin
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<StandardResponse> createEvent(@RequestBody @Valid EventRequestDTO eventRequestDTO){
        EventResponseDTO newEvent = eventService.createEvent(eventRequestDTO);
        return new ResponseEntity<>(
                new StandardResponse(201,"Event created successfully",newEvent),
                HttpStatus.CREATED
        );
    }
    @GetMapping
    public ResponseEntity<StandardResponse> getAllEvents(){
        List<EventResponseDTO> events = eventService.getAllEvents();
        return new ResponseEntity<>(
                new StandardResponse(200,"Successfully fetched all events",events),
                HttpStatus.OK
        );

    }

    @GetMapping("/{eventId}")
    public ResponseEntity<StandardResponse> getEventById(@PathVariable Long eventId){
        EventResponseDTO event = eventService.getEventById(eventId);
        return new ResponseEntity<>(
                new StandardResponse(200,"Event found successfully",event),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/search")
    public ResponseEntity<StandardResponse> searchEvent(@RequestParam String word){
        List<EventResponseDTO> events = eventService.searchEvents(word);
        return new ResponseEntity<>(
                new StandardResponse(200,"Events fetched successfully",events),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/page")
    public ResponseEntity<StandardResponse> getPageEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Page<EventResponseDTO> eventsPage = eventService.getPagedEvents(page, size, sortBy, sortDir);
        return new ResponseEntity<>(
                new StandardResponse(200,"Page fetched successfully",eventsPage),
                HttpStatus.OK
        );

    }
}

