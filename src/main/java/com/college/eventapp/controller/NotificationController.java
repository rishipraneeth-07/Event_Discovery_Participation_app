package com.college.eventapp.controller;

import com.college.eventapp.dto.NotificationResponseDTO;
import com.college.eventapp.model.Notification;
import com.college.eventapp.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/users/{userId}/notifications")
    public List<NotificationResponseDTO> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/notifications/{id}/read")
    public NotificationResponseDTO markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(id);
        dto.setRead(true);
        return dto;
    }

    private NotificationResponseDTO convertToDTO(Notification n) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(n.getId());
        dto.setUserId(n.getUser().getId());
        dto.setMessage(n.getMessage());
        dto.setRead(n.isRead());

        String msg = n.getMessage().toUpperCase();
        if (msg.contains("APPROVED"))       dto.setType("EVENT_APPROVED");
        else if (msg.contains("REJECTED"))  dto.setType("EVENT_REJECTED");
        else if (msg.contains("REGISTER"))  dto.setType("REGISTRATION");
        else                                dto.setType("GENERAL");

        if (n.getCreatedAt() != null) {
            dto.setCreatedAt(n.getCreatedAt()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        return dto;
    }
}