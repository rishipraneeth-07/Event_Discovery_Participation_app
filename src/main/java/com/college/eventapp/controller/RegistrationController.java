package com.college.eventapp.controller;

import com.college.eventapp.dto.RegistrationResponseDTO;
import com.college.eventapp.model.Registration;
import com.college.eventapp.service.NotificationService;
import com.college.eventapp.service.RegistrationService;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final NotificationService notificationService;

    public RegistrationController(RegistrationService registrationService,
                                  NotificationService notificationService) {
        this.registrationService = registrationService;
        this.notificationService = notificationService;
    }

    @PostMapping("/events/{eventId}/register")
    public RegistrationResponseDTO registerForEvent(@PathVariable Long eventId,
                                                    @RequestParam Long userId) {
        Registration reg = registrationService.registerForEvent(userId, eventId);

        // ✅ Notify student
        notificationService.createNotification(
                userId,
                "You have successfully registered for '" + reg.getEvent().getTitle() + "'. See you there!"
        );

        // ✅ Notify organizer
        notificationService.createNotification(
                reg.getEvent().getOrganizer().getId(),
                "A new student '" + reg.getUser().getName() + "' registered for your event '" +
                        reg.getEvent().getTitle() + "'"
        );

        return convertToDTO(reg);
    }

    // ✅ NEW - Cancel registration
    @DeleteMapping("/registrations/{registrationId}")
    public String cancelRegistration(@PathVariable Long registrationId) {
        registrationService.cancelRegistration(registrationId);
        return "Registration cancelled successfully";
    }

    // ✅ NEW - Check if user already registered
    @GetMapping("/events/{eventId}/is-registered")
    public boolean isUserRegistered(@PathVariable Long eventId,
                                    @RequestParam Long userId) {
        return registrationService.isUserRegistered(userId, eventId);
    }

    @GetMapping("/users/{userId}/registrations")
    public List<RegistrationResponseDTO> getUserRegistrations(@PathVariable Long userId) {
        return registrationService.getUserRegistrations(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{eventId}/registrations")
    public List<RegistrationResponseDTO> getEventRegistrations(@PathVariable Long eventId) {
        return registrationService.getEventRegistrations(eventId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RegistrationResponseDTO convertToDTO(Registration reg) {
        RegistrationResponseDTO dto = new RegistrationResponseDTO();
        dto.setId(reg.getId());
        dto.setUserId(reg.getUser().getId());
        dto.setUserName(reg.getUser().getName());
        dto.setEventId(reg.getEvent().getId());
        dto.setEventTitle(reg.getEvent().getTitle());
        dto.setEventLocation(reg.getEvent().getLocation());
        dto.setStatus("CONFIRMED");

        if (reg.getEvent().getEventDateTime() != null) {
            dto.setEventDate(reg.getEvent().getEventDateTime()
                    .toLocalDate().toString());
        }
        if (reg.getRegistrationDate() != null) {
            dto.setRegisteredAt(reg.getRegistrationDate()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        return dto;
    }
}