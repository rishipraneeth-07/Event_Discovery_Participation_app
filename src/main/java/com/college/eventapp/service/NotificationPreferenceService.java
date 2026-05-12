package com.college.eventapp.service;

import com.college.eventapp.dto.NotificationPreferenceDTO;

public interface NotificationPreferenceService {
    NotificationPreferenceDTO getPreferences(Long userId);
    NotificationPreferenceDTO updatePreferences(Long userId, NotificationPreferenceDTO request);
}
