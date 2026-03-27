package com.college.eventapp.dto;

import lombok.Data;

@Data
public class RegistrationResponseDTO {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String eventDate;
    private String eventLocation;
    private Long userId;
    private String userName;
    private String status;
    private String registeredAt;
}
