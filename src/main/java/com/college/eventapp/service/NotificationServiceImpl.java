package com.college.eventapp.service;

import com.college.eventapp.dto.NotificationResponseDTO;
import com.college.eventapp.model.Role;
import com.college.eventapp.exception.ResourceNotFoundException;
import com.college.eventapp.mapper.NotificationMapper;
import com.college.eventapp.model.Notification;
import com.college.eventapp.model.NotificationPreference;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.NotificationPreferenceRepository;
import com.college.eventapp.repository.NotificationRepository;
import com.college.eventapp.repository.UserRepository;
import com.college.eventapp.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationPreferenceRepository notificationPreferenceRepository,
                                   UserRepository userRepository,
                                   CurrentUserService currentUserService,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional
    public Notification createNotification(Long userId, String message, String type) {
        return createNotification(userId, message, type, null);
    }

    @Override
    @Transactional
    public Notification createMandatoryNotification(Long userId, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public Notification createNotification(Long userId, String message, String type, String preferenceType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        NotificationPreference preference = notificationPreferenceRepository.findByUser(user)
                .orElseGet(() -> createDefaultPreference(user));
        if (!isEnabled(preference, preferenceType)) {
            return null;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        currentUserService.requireSameUserOrAdmin(userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getCurrentUserNotifications() {
        return getUserNotifications(currentUserService.getCurrentUser().id());
    }

    @Override
    @Transactional
    public NotificationResponseDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!currentUserService.hasRole(Role.ADMIN)) {
            currentUserService.requireSameUser(notification.getUser().getId());
        }

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void markAllReadForCurrentUser() {
        notificationRepository.markAllReadByUserId(currentUserService.getCurrentUser().id());
    }

    private NotificationPreference createDefaultPreference(User user) {
        NotificationPreference preference = new NotificationPreference();
        preference.setUser(user);
        preference.setEventApproval(true);
        preference.setRegistrationUpdates(true);
        preference.setReminders(true);
        preference.setMarketing(false);
        return notificationPreferenceRepository.save(preference);
    }

    private boolean isEnabled(NotificationPreference preference, String preferenceType) {
        if (preferenceType == null || preferenceType.isBlank()) {
            return true;
        }

        return switch (preferenceType) {
            case "eventApproval" -> preference.isEventApproval();
            case "registrationUpdates" -> preference.isRegistrationUpdates();
            case "reminders" -> preference.isReminders();
            case "marketing" -> preference.isMarketing();
            default -> true;
        };
    }
}
