package com.college.eventapp.controller;

import com.college.eventapp.dto.NotificationResponseDTO;
import com.college.eventapp.dto.MessageResponseDTO;
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

    @GetMapping("/notifications")
    public List<NotificationResponseDTO> getCurrentUserNotifications() {
        return notificationService.getCurrentUserNotifications();
    }

    @GetMapping("/users/{userId}/notifications")
    public List<NotificationResponseDTO> getUserNotifications(@PathVariable @Positive(message = "User id must be positive") Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    @PutMapping("/notifications/{id}/read")
    public MessageResponseDTO markAsRead(@PathVariable @Positive(message = "Notification id must be positive") Long id) {
        notificationService.markAsRead(id);
        return new MessageResponseDTO("Notification marked as read");
    }

    @PutMapping("/notifications/read-all")
    public MessageResponseDTO markAllAsRead() {
        notificationService.markAllReadForCurrentUser();
        return new MessageResponseDTO("Notifications marked as read");
    }
}
