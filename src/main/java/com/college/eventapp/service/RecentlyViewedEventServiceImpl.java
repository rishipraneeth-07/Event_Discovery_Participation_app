package com.college.eventapp.service;

import com.college.eventapp.exception.BadRequestException;
import com.college.eventapp.exception.ResourceNotFoundException;
import com.college.eventapp.model.Event;
import com.college.eventapp.model.RecentlyViewedEvent;
import com.college.eventapp.model.Role;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.EventRepository;
import com.college.eventapp.repository.RecentlyViewedEventRepository;
import com.college.eventapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecentlyViewedEventServiceImpl implements RecentlyViewedEventService {

    private final RecentlyViewedEventRepository recentlyViewedEventRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RecentlyViewedEventServiceImpl(RecentlyViewedEventRepository recentlyViewedEventRepository,
                                          UserRepository userRepository,
                                          EventRepository eventRepository) {
        this.recentlyViewedEventRepository = recentlyViewedEventRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public void recordView(Long userId, Long eventId) {
        User user = getStudentUser(userId);
        Event event = getEvent(eventId);

        RecentlyViewedEvent viewedEvent = recentlyViewedEventRepository.findByUserAndEvent(user, event)
                .orElseGet(RecentlyViewedEvent::new);
        viewedEvent.setUser(user);
        viewedEvent.setEvent(event);
        viewedEvent.setViewedAt(LocalDateTime.now());
        recentlyViewedEventRepository.save(viewedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getRecentlyViewedEvents(Long userId, int limit) {
        User user = getStudentUser(userId);
        return recentlyViewedEventRepository.findByUserOrderByViewedAtDesc(user)
                .stream()
                .limit(limit)
                .map(RecentlyViewedEvent::getEvent)
                .toList();
    }

    private User getStudentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only student users can manage recently viewed events");
        }
        return user;
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }
}
