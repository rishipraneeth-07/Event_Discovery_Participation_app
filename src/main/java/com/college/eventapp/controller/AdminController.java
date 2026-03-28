package com.college.eventapp.controller;

import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.model.Event;
import com.college.eventapp.repository.RegistrationRepository;
import com.college.eventapp.service.EventService;
import com.college.eventapp.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin/events")
public class AdminController {

    private final EventService eventService;
    private final RegistrationRepository registrationRepository;
    private final NotificationService notificationService;

    public AdminController(EventService eventService,
                           RegistrationRepository registrationRepository,
                           NotificationService notificationService) {
        this.eventService = eventService;
        this.registrationRepository = registrationRepository;
        this.notificationService = notificationService;
    }

    @PutMapping("/{id}/approve")
    public EventResponseDTO approveEvent(@PathVariable Long id) {
        Event event = eventService.approveEvent(id);

        // ✅ Notify organizer
        notificationService.createNotification(
                event.getOrganizer().getId(),
                "Your event '" + event.getTitle() + "' has been approved! Students can now register."
        );

        // ✅ Notify all registered students
        registrationRepository.findByEvent(event).forEach(reg ->
                notificationService.createNotification(
                        reg.getUser().getId(),
                        "Good news! The event '" + event.getTitle() + "' you registered for has been approved."
                )
        );

        return convertToDTO(event);
    }

    @PutMapping("/{id}/reject")
    public EventResponseDTO rejectEvent(@PathVariable Long id) {
        Event event = eventService.rejectEvent(id);

        // ✅ Notify organizer
        notificationService.createNotification(
                event.getOrganizer().getId(),
                "Your event '" + event.getTitle() + "' has been rejected. Please review and resubmit."
        );

        return convertToDTO(event);
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