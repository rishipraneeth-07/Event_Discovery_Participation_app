package com.college.eventapp.dto;

import lombok.Data;

@Data
public class NotificationPreferenceDTO {
    private boolean eventApproval;
    private boolean registrationUpdates;
    private boolean reminders;
    private boolean marketing;
}
