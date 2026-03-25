package com.college.eventapp.controller;

import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.model.Event;
import com.college.eventapp.service.EventService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/events")
public class AdminController {
    private final EventService eventService;

    public AdminController(EventService eventService) {
        this.eventService = eventService;
    }

    @PutMapping("/{id}/approve")
    public EventResponseDTO approveEvent(@PathVariable Long id) {
        Event event = eventService.approveEvent(id);
        return convertToDTO(event);
    }

    @PutMapping("/{id}/reject")
    public EventResponseDTO rejectEvent(@PathVariable Long id) {
        Event event = eventService.rejectEvent(id);
        return convertToDTO(event);
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
