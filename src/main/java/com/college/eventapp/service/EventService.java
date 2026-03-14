package com.college.eventapp.service;

import com.college.eventapp.model.Event;

import java.util.List;

public interface EventService {
    Event createEvent(Event event);
    Event updateEvent(Long id, Event event);
    void deleteEvent(Long id);
    Event getEventById(Long id);
    List<Event> getAllEvents();
    List<Event> getApprovedEvents();
    List<Event> getEventsByOrganizer(Long organizerId);
    Event approveEvent(Long eventId);
    Event rejectEvent(Long eventId);
}
