package com.college.eventapp.service;

import com.college.eventapp.model.Event;

import java.util.List;

public interface RecentlyViewedEventService {
    void recordView(Long userId, Long eventId);
    List<Event> getRecentlyViewedEvents(Long userId, int limit);
}
