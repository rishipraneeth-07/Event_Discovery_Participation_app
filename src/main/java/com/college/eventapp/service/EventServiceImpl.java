package com.college.eventapp.service;

import com.college.eventapp.model.Event;
import com.college.eventapp.model.EventStatus;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.EventRepository;
import com.college.eventapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    private EventRepository eventRepository;
    private UserRepository userRepository;
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Event createEvent(Event event) {

        Long organizerId = event.getOrganizer().getId();

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        event.setOrganizer(organizer);
        event.setStatus(EventStatus.PENDING);

        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Long id, Event updatedEvent) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setLocation(updatedEvent.getLocation());
        event.setEventDateTime(updatedEvent.getEventDateTime());

        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        eventRepository.delete(event);
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> getApprovedEvents() {
        return eventRepository.findByStatus(EventStatus.APPROVED);
    }

    @Override
    public List<Event> getEventsByOrganizer(Long organizerId) {

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        return eventRepository.findByOrganizer(organizer);
    }

    @Override
    public Event approveEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setStatus(EventStatus.APPROVED);

        return eventRepository.save(event);
    }

    @Override
    public Event rejectEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setStatus(EventStatus.REJECTED);

        return eventRepository.save(event);
    }
}
