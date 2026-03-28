package com.college.eventapp.dto;

import lombok.Data;

@Data
public class AdminStatsDTO {
    private long totalEvents;
    private long pendingEvents;
    private long approvedEvents;
    private long rejectedEvents;
    private long totalUsers;
    private long totalStudents;
    private long totalOrganizers;
    private long totalRegistrations;
}