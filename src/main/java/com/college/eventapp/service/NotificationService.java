package com.college.eventapp.service;

import com.college.eventapp.model.Notification;

import java.util.List;

public interface NotificationService {
    Notification createNotification(Long userId, String message);
    List<Notification> getUserNotifications(Long userId);
    void markAsRead(Long notificationId);
}
