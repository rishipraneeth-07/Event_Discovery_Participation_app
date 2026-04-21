package com.college.eventapp.dto;

import lombok.Data;

@Data
public class EventAnalyticsResponseDTO {
    private Long eventId;
    private long registrations;
    private int capacity;
    private int occupancyPercent;
    private String status;
    private String category;
    private long views;
    private long savedCount;
    private long notificationCount;
}
