package com.college.eventapp.controller;

import com.college.eventapp.dto.NotificationPreferenceDTO;
import com.college.eventapp.security.CurrentUserService;
import com.college.eventapp.service.NotificationPreferenceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api")
public class NotificationPreferenceController {

    private final NotificationPreferenceService notificationPreferenceService;
    private final CurrentUserService currentUserService;

    public NotificationPreferenceController(NotificationPreferenceService notificationPreferenceService,
                                            CurrentUserService currentUserService) {
        this.notificationPreferenceService = notificationPreferenceService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/notification-preferences")
    public NotificationPreferenceDTO getCurrentUserPreferences() {
        return notificationPreferenceService.getPreferences(currentUserService.getCurrentUser().id());
    }

    @PutMapping("/notification-preferences")
    public NotificationPreferenceDTO updateCurrentUserPreferences(@Valid @RequestBody NotificationPreferenceDTO request) {
        return notificationPreferenceService.updatePreferences(currentUserService.getCurrentUser().id(), request);
    }

    @GetMapping("/users/{userId}/notification-preferences")
    public NotificationPreferenceDTO getPreferencesLegacy(@PathVariable @Positive(message = "User id must be positive") Long userId) {
        return notificationPreferenceService.getPreferences(userId);
    }

    @PutMapping("/users/{userId}/notification-preferences")
    public NotificationPreferenceDTO updatePreferencesLegacy(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                                             @Valid @RequestBody NotificationPreferenceDTO request) {
        return notificationPreferenceService.updatePreferences(userId, request);
    }
}
