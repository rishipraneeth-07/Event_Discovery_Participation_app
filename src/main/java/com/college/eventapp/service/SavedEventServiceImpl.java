package com.college.eventapp.service;

import com.college.eventapp.exception.BadRequestException;
import com.college.eventapp.exception.ResourceNotFoundException;
import com.college.eventapp.model.Event;
import com.college.eventapp.model.Role;
import com.college.eventapp.model.SavedEvent;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.EventRepository;
import com.college.eventapp.repository.SavedEventRepository;
import com.college.eventapp.repository.UserRepository;
import com.college.eventapp.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SavedEventServiceImpl implements SavedEventService {

    private final SavedEventRepository savedEventRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CurrentUserService currentUserService;

    public SavedEventServiceImpl(SavedEventRepository savedEventRepository,
                                 UserRepository userRepository,
                                 EventRepository eventRepository,
                                 CurrentUserService currentUserService) {
        this.savedEventRepository = savedEventRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public Event saveEvent(Long userId, Long eventId) {
        User user = getStudentUser(userId);
        Event event = getEvent(eventId);

        if (!savedEventRepository.existsByUserAndEvent(user, event)) {
            SavedEvent savedEvent = new SavedEvent();
            savedEvent.setUser(user);
            savedEvent.setEvent(event);
            savedEvent.setCreatedAt(LocalDateTime.now());
            savedEventRepository.save(savedEvent);
        }

        return event;
    }

    @Override
    @Transactional
    public void unsaveEvent(Long userId, Long eventId) {
        User user = getStudentUser(userId);
        Event event = getEvent(eventId);

        savedEventRepository.findByUserAndEvent(user, event)
                .ifPresent(savedEventRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getSavedEvents(Long userId) {
        User user = getStudentUser(userId);
        return savedEventRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(SavedEvent::getEvent)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEventSaved(Long userId, Long eventId) {
        User user = getStudentUser(userId);
        Event event = getEvent(eventId);
        return savedEventRepository.existsByUserAndEvent(user, event);
    }

    private User getStudentUser(Long userId) {
        currentUserService.requireSameUser(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only student users can manage saved events");
        }
        return user;
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }
}
