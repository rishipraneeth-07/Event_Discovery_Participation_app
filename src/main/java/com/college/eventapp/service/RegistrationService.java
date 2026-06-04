package com.college.eventapp.service;

import com.college.eventapp.model.Registration;

import java.util.List;

public interface RegistrationService {
    Registration registerForEvent(Long userId, Long eventId);
    List<Registration> getUserRegistrations(Long userId);
    List<Registration> getEventRegistrations(Long eventId);
    Registration cancelRegistration(Long registrationId);
    Registration getUserRegistration(Long userId, Long eventId);
    boolean isUserRegistered(Long userId, Long eventId);
}
