package com.college.eventapp.controller;

import com.college.eventapp.dto.IsRegisteredDTO;
import com.college.eventapp.dto.MessageResponseDTO;
import com.college.eventapp.dto.RegistrationResponseDTO;
import com.college.eventapp.model.Registration;
import com.college.eventapp.security.CurrentUserService;
import com.college.eventapp.service.RegistrationService;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api")
public class RegistrationController {

    private static final DateTimeFormatter REGISTRATION_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final RegistrationService registrationService;
    private final CurrentUserService currentUserService;

    public RegistrationController(RegistrationService registrationService,
                                  CurrentUserService currentUserService) {
        this.registrationService = registrationService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/registrations/events/{eventId}/register")
    public RegistrationResponseDTO registerForCurrentUser(
            @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        Registration registration = registrationService.registerForEvent(currentUserService.getCurrentUser().id(), eventId);
        return convertToDTO(registration);
    }

    @DeleteMapping("/registrations/events/{eventId}/cancel")
    public MessageResponseDTO cancelCurrentUserRegistration(
            @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        Registration registration = registrationService.getUserRegistration(currentUserService.getCurrentUser().id(), eventId);
        if (registration == null) {
            throw new com.college.eventapp.exception.ResourceNotFoundException("Registration not found");
        }
        registrationService.cancelRegistration(registration.getId());
        return new MessageResponseDTO("Registration cancelled successfully");
    }

    @GetMapping("/registrations/my")
    public List<RegistrationResponseDTO> getCurrentUserRegistrations() {
        return registrationService.getUserRegistrations(currentUserService.getCurrentUser().id())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @GetMapping("/registrations/events/{eventId}/check")
    public IsRegisteredDTO isCurrentUserRegistered(
            @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        Registration registration = registrationService.getUserRegistration(currentUserService.getCurrentUser().id(), eventId);
        IsRegisteredDTO dto = new IsRegisteredDTO();
        dto.setRegistered(registration != null);
        if (registration != null) {
            dto.setRegistrationId(registration.getId());
        }
        return dto;
    }

    @PostMapping("/events/{eventId}/register")
    public RegistrationResponseDTO registerForEventLegacy(
            @PathVariable @Positive(message = "Event id must be positive") Long eventId,
            @RequestParam @Positive(message = "User id must be positive") Long userId) {
        Registration reg = registrationService.registerForEvent(userId, eventId);
        return convertToDTO(reg);
    }

    @DeleteMapping("/registrations/{registrationId}")
    public MessageResponseDTO cancelRegistrationLegacy(
            @PathVariable @Positive(message = "Registration id must be positive") Long registrationId) {
        registrationService.cancelRegistration(registrationId);
        return new MessageResponseDTO("Registration cancelled successfully");
    }

    @GetMapping("/events/{eventId}/is-registered")
    public IsRegisteredDTO isUserRegisteredLegacy(
            @PathVariable @Positive(message = "Event id must be positive") Long eventId,
            @RequestParam @Positive(message = "User id must be positive") Long userId) {
        Registration registration = registrationService.getUserRegistration(userId, eventId);
        IsRegisteredDTO dto = new IsRegisteredDTO();
        dto.setRegistered(registration != null);
        if (registration != null) {
            dto.setRegistrationId(registration.getId());
        }
        return dto;
    }

    @GetMapping("/users/{userId}/registrations")
    public List<RegistrationResponseDTO> getUserRegistrationsLegacy(
            @PathVariable @Positive(message = "User id must be positive") Long userId) {
        return registrationService.getUserRegistrations(userId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @GetMapping("/events/{eventId}/registrations")
    public List<RegistrationResponseDTO> getEventRegistrations(
            @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        return registrationService.getEventRegistrations(eventId)
                .stream()
                .map(this::convertToDTO)
                .toList();
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
            dto.setEventDateTime(reg.getEvent().getEventDateTime());
            dto.setEventDate(reg.getEvent().getEventDateTime().toLocalDate().toString());
        }
        if (reg.getRegistrationDate() != null) {
            dto.setRegisteredAt(reg.getRegistrationDate().format(REGISTRATION_TIMESTAMP));
        }

        return dto;
    }
}
