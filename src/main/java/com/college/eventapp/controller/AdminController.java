package com.college.eventapp.controller;

import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.model.Event;
import com.college.eventapp.repository.RegistrationRepository;
import com.college.eventapp.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin/events")
public class AdminController {

    private final EventService eventService;
    private final RegistrationRepository registrationRepository;

    public AdminController(EventService eventService,
                           RegistrationRepository registrationRepository) {
        this.eventService = eventService;
        this.registrationRepository = registrationRepository;
    }

    @PutMapping("/{id}/approve")
    public EventResponseDTO approveEvent(@PathVariable Long id) {
        return convertToDTO(eventService.approveEvent(id));
    }

    @PutMapping("/{id}/reject")
    public EventResponseDTO rejectEvent(@PathVariable Long id) {
        return convertToDTO(eventService.rejectEvent(id));
    }

    private EventResponseDTO convertToDTO(Event event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setCategory(event.getCategory() != null ? event.getCategory() : "General");
        dto.setCapacity(event.getCapacity() != null ? event.getCapacity() : 0);
        dto.setStatus(event.getStatus());
        dto.setOrganizerId(event.getOrganizer().getId());
        dto.setOrganizerName(event.getOrganizer().getName());

        if (event.getEventDateTime() != null) {
            dto.setDate(event.getEventDateTime().toLocalDate().toString());
            dto.setTime(event.getEventDateTime().toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            dto.setDate("");
            dto.setTime("");
        }

        dto.setRegisteredCount(registrationRepository.findByEvent(event).size());
        return dto;
    }
}