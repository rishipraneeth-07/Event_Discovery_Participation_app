package com.college.eventapp.controller;

import com.college.eventapp.dto.IsRegisteredDTO;
import com.college.eventapp.dto.MessageResponseDTO;
import com.college.eventapp.dto.RegistrationResponseDTO;
import jakarta.validation.constraints.Positive;
import com.college.eventapp.model.Registration;
import com.college.eventapp.service.NotificationService;
import com.college.eventapp.service.RegistrationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/api")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final NotificationService notificationService;

    public RegistrationController(RegistrationService registrationService,
                                  NotificationService notificationService) {
        this.registrationService = registrationService;
        this.notificationService = notificationService;
    }

    // POST /api/events/{eventId}/register?userId={userId}
    @PostMapping("/events/{eventId}/register")
    public RegistrationResponseDTO registerForEvent(@PathVariable @Positive(message = "Event id must be positive") Long eventId,
                                                    @RequestParam @Positive(message = "User id must be positive") Long userId) {
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

    // DELETE /api/registrations/{registrationId}
    @DeleteMapping("/registrations/{registrationId}")
    public MessageResponseDTO cancelRegistration(@PathVariable @Positive(message = "Registration id must be positive") Long registrationId) {
        registrationService.cancelRegistration(registrationId);
        return new MessageResponseDTO("Registration cancelled successfully");
    }

    // GET /api/events/{eventId}/is-registered?userId={userId}
    @GetMapping("/events/{eventId}/is-registered")
    public IsRegisteredDTO isUserRegistered(@PathVariable @Positive(message = "Event id must be positive") Long eventId,
                                            @RequestParam @Positive(message = "User id must be positive") Long userId) {
        boolean registered = registrationService.isUserRegistered(userId, eventId);
        IsRegisteredDTO dto = new IsRegisteredDTO();
        dto.setRegistered(registered);
        return dto;
    }

    // GET /api/users/{userId}/registrations
    @GetMapping("/users/{userId}/registrations")
    public List<RegistrationResponseDTO> getUserRegistrations(@PathVariable @Positive(message = "User id must be positive") Long userId) {
        return registrationService.getUserRegistrations(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // GET /api/events/{eventId}/registrations
    @GetMapping("/events/{eventId}/registrations")
    public List<RegistrationResponseDTO> getEventRegistrations(@PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        return registrationService.getEventRegistrations(eventId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

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
