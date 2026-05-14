package com.college.eventapp.service;

import com.college.eventapp.dto.NotificationPreferenceDTO;
import com.college.eventapp.exception.ResourceNotFoundException;
import com.college.eventapp.model.NotificationPreference;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.NotificationPreferenceRepository;
import com.college.eventapp.repository.UserRepository;
import com.college.eventapp.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public NotificationPreferenceServiceImpl(NotificationPreferenceRepository notificationPreferenceRepository,
                                             UserRepository userRepository,
                                             CurrentUserService currentUserService) {
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationPreferenceDTO getPreferences(Long userId) {
        return toDto(getOrCreatePreference(userId));
    }

    @Override
    @Transactional
    public NotificationPreferenceDTO updatePreferences(Long userId, NotificationPreferenceDTO request) {
        NotificationPreference preference = getOrCreatePreference(userId);
        preference.setEventApproval(request.isEventApproval());
        preference.setRegistrationUpdates(request.isRegistrationUpdates());
        preference.setReminders(request.isReminders());
        preference.setMarketing(request.isMarketing());
        return toDto(notificationPreferenceRepository.save(preference));
    }

    private NotificationPreference getOrCreatePreference(Long userId) {
        currentUserService.requireSameUserOrAdmin(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationPreferenceRepository.findByUser(user)
                .orElseGet(() -> {
                    NotificationPreference preference = new NotificationPreference();
                    preference.setUser(user);
                    preference.setEventApproval(true);
                    preference.setRegistrationUpdates(true);
                    preference.setReminders(true);
                    preference.setMarketing(false);
                    return notificationPreferenceRepository.save(preference);
                });
    }

    private NotificationPreferenceDTO toDto(NotificationPreference preference) {
        NotificationPreferenceDTO dto = new NotificationPreferenceDTO();
        dto.setEventApproval(preference.isEventApproval());
        dto.setRegistrationUpdates(preference.isRegistrationUpdates());
        dto.setReminders(preference.isReminders());
        dto.setMarketing(preference.isMarketing());
        return dto;
    }
}
