package com.college.eventapp.dto;

import lombok.Data;

@Data
public class NotificationResponseDTO {
    private Long id;
    private Long userId;
    private String message;
    private String type;
    private boolean read;
    private String createdAt;
}
