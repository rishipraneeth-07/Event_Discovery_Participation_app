package com.college.eventapp.dto;

import lombok.Data;

@Data
public class EventAnalyticsResponseDTO {
    private Long eventId;
    private String title;
    private int totalCapacity;
    private long registeredCount;
    private int availableSpots;
    private double fillRate;
    private String status;
}
