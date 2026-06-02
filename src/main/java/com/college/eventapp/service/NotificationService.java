package com.college.eventapp.service;

import com.college.eventapp.dto.NotificationResponseDTO;
import com.college.eventapp.model.Notification;

import java.util.List;

public interface NotificationService {
    Notification createNotification(Long userId, String message, String type);
    Notification createNotification(Long userId, String message, String type, String preferenceType);
    Notification createMandatoryNotification(Long userId, String message, String type);
    List<NotificationResponseDTO> getUserNotifications(Long userId);
    List<NotificationResponseDTO> getCurrentUserNotifications();
    NotificationResponseDTO markAsRead(Long notificationId);
    void markAllReadForCurrentUser();
}
