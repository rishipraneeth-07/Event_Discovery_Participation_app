package com.college.eventapp.dto;

import com.college.eventapp.model.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class EventResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDateTime;
    private EventStatus status;
    private String date;
    private String time;
    private String category;
    private Integer capacity;
    private Integer registeredCount;
    private Long organizerId;
    private String organizerName;
}
