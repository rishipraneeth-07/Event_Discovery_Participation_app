package com.college.eventapp.mapper;

import com.college.eventapp.dto.NotificationResponseDTO;
import com.college.eventapp.model.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class NotificationMapper {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DEFAULT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.ENGLISH);

    public NotificationResponseDTO toDTO(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setMessage(notification.getMessage());
        dto.setType(resolveType(notification.getMessage()));
        dto.setRead(notification.isRead());
        dto.setCreatedAt(formatCreatedAt(notification.getCreatedAt()));
        return dto;
    }

    private String resolveType(String message) {
        if (message == null || message.isBlank()) {
            return "GENERAL";
        }

        String normalized = message.toUpperCase(Locale.ENGLISH);
        if (normalized.contains("APPROV")) {
            return "APPROVAL";
        }
        if (normalized.contains("REJECT")) {
            return "REJECTION";
        }
        if (normalized.contains("REGISTR")) {
            return "REGISTRATION";
        }
        if (normalized.contains("CANCEL")) {
            return "EVENT_UPDATE";
        }
        return "GENERAL";
    }

    private String formatCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            return "";
        }

        LocalDate today = LocalDate.now();
        LocalDate createdDate = createdAt.toLocalDate();
        if (createdDate.equals(today)) {
            return "Today, " + createdAt.format(TIME_FORMAT);
        }
        if (createdDate.equals(today.minusDays(1))) {
            return "Yesterday, " + createdAt.format(TIME_FORMAT);
        }
        return createdAt.format(DEFAULT_FORMAT);
    }
}
