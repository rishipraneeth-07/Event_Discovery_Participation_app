package com.college.eventapp.service;

import com.college.eventapp.model.Event;

import java.util.List;

public interface RecommendationService {
    List<Event> getRecommendedEvents(Long userId, int limit);
}
