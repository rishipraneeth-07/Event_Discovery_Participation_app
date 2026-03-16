package com.college.eventapp.service;

import com.college.eventapp.model.Event;
import com.college.eventapp.model.Registration;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.EventRepository;
import com.college.eventapp.repository.RegistrationRepository;
import com.college.eventapp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

public class RegistrationServiceImpl implements RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RegistrationServiceImpl(RegistrationRepository registrationRepository,
                                   UserRepository userRepository,
                                   EventRepository eventRepository) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Registration registerForEvent(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (registrationRepository.existsByUserAndEvent(user, event)) {
            throw new RuntimeException("User already registered for this event");
        }

        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setRegistrationDate(LocalDateTime.now());

        return registrationRepository.save(registration);
    }

    @Override
    public List<Registration> getUserRegistrations(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return registrationRepository.findByUser(user);
    }

    @Override
    public List<Registration> getEventRegistrations(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return registrationRepository.findByEvent(event);
    }
}
