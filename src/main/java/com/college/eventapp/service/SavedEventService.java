package com.college.eventapp.service;

import com.college.eventapp.model.Event;

import java.util.List;

public interface SavedEventService {
    Event saveEvent(Long userId, Long eventId);
    void unsaveEvent(Long userId, Long eventId);
    List<Event> getSavedEvents(Long userId);
    boolean isEventSaved(Long userId, Long eventId);
}
