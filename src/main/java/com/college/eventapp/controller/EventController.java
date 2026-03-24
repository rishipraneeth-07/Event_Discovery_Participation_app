package com.college.eventapp.controller;

import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.model.Event;
import com.college.eventapp.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public EventResponseDTO createEvent(@RequestBody Event event) {
        Event savedEvent = eventService.createEvent(event);
        return convertToDTO(savedEvent);
    }

    @PutMapping("/{id}")
    public EventResponseDTO updateEvent(@PathVariable Long id, @RequestBody Event event) {
        Event updatedEvent = eventService.updateEvent(id, event);
        return convertToDTO(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "Event deleted successfully";
    }

    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return convertToDTO(event);
    }

    @GetMapping
    public List<EventResponseDTO> getApprovedEvents() {
        List<Event> events = eventService.getApprovedEvents();
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EventResponseDTO convertToDTO(Event event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setEventDateTime(event.getEventDateTime());
        dto.setStatus(event.getStatus());
        dto.setOrganizerId(event.getOrganizer().getId());
        dto.setOrganizerName(event.getOrganizer().getName());
        return dto;
    }
}
