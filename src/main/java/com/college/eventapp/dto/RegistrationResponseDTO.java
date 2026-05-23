package com.college.eventapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegistrationResponseDTO {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime eventDateTime;
    private String eventDate;
    private String eventLocation;
    private Long userId;
    private String userName;
    private String status;
    private String registeredAt;
}
