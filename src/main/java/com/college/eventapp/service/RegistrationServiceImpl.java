package com.college.eventapp.service;

import com.college.eventapp.exception.BadRequestException;
import com.college.eventapp.exception.ResourceNotFoundException;
import com.college.eventapp.model.Event;
import com.college.eventapp.model.EventStatus;
import com.college.eventapp.model.Registration;
import com.college.eventapp.model.Role;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.EventRepository;
import com.college.eventapp.repository.RegistrationRepository;
import com.college.eventapp.repository.UserRepository;
import com.college.eventapp.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    public RegistrationServiceImpl(RegistrationRepository registrationRepository,
                                   UserRepository userRepository,
                                   EventRepository eventRepository,
                                   CurrentUserService currentUserService,
                                   NotificationService notificationService) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.currentUserService = currentUserService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Registration registerForEvent(Long userId, Long eventId) {
        currentUserService.requireSameUser(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only student users can register for events");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        if (event.getStatus() != EventStatus.APPROVED) {
            throw new BadRequestException("Only approved events can be registered");
        }

        if (registrationRepository.existsByUserAndEvent(user, event)) {
            throw new BadRequestException("Already registered for this event");
        }

        long currentCount = registrationRepository.countByEvent(event);
        if (event.getCapacity() != null && event.getCapacity() > 0
                && currentCount >= event.getCapacity()) {
            throw new BadRequestException("Event is full");
        }

        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setRegistrationDate(LocalDateTime.now());

        Registration saved = registrationRepository.save(registration);
        notificationService.createNotification(
                user.getId(),
                "You are registered for \"" + event.getTitle() + "\".",
                "REGISTRATION",
                "registrationUpdates"
        );
        notificationService.createNotification(
                event.getOrganizer().getId(),
                user.getName() + " registered for your event \"" + event.getTitle() + "\".",
                "REGISTRATION",
                "registrationUpdates"
        );
        return saved;
    }

    @Override
    public List<Registration> getUserRegistrations(Long userId) {
        currentUserService.requireSameUserOrAdmin(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return registrationRepository.findByUserOrderByEventEventDateTimeDesc(user);
    }

    @Override
    public List<Registration> getEventRegistrations(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        if (!currentUserService.hasRole(Role.ADMIN)) {
            currentUserService.requireRole(Role.ORGANIZER);
            currentUserService.requireSameUserOrAdmin(event.getOrganizer().getId());
        }
        return registrationRepository.findByEvent(event);
    }

    @Override
    @Transactional
    public Registration cancelRegistration(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        if (!currentUserService.hasRole(Role.ADMIN)) {
            currentUserService.requireSameUser(registration.getUser().getId());
        }
        registrationRepository.delete(registration);
        notificationService.createNotification(
                registration.getUser().getId(),
                "Your registration for \"" + registration.getEvent().getTitle() + "\" was cancelled.",
                "REGISTRATION",
                "registrationUpdates"
        );
        notificationService.createNotification(
                registration.getEvent().getOrganizer().getId(),
                registration.getUser().getName() + " cancelled registration for \"" + registration.getEvent().getTitle() + "\".",
                "REGISTRATION",
                "registrationUpdates"
        );
        return registration;
    }

    @Override
    public Registration getUserRegistration(Long userId, Long eventId) {
        currentUserService.requireSameUserOrAdmin(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return registrationRepository.findByUserAndEvent(user, event).orElse(null);
    }

    @Override
    public boolean isUserRegistered(Long userId, Long eventId) {
        return getUserRegistration(userId, eventId) != null;
    }
}
