package com.college.eventapp.controller;

import com.college.eventapp.dto.NotificationResponseDTO;
import jakarta.validation.constraints.Positive;
import com.college.eventapp.service.NotificationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/users/{userId}/notifications")
    public List<NotificationResponseDTO> getUserNotifications(@PathVariable @Positive(message = "User id must be positive") Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    @PutMapping("/notifications/{id}/read")
    public NotificationResponseDTO markAsRead(@PathVariable @Positive(message = "Notification id must be positive") Long id) {
        return notificationService.markAsRead(id);
    }
}
