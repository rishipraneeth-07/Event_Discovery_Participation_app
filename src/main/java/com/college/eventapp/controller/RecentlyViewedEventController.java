package com.college.eventapp.controller;

import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.dto.MessageResponseDTO;
import com.college.eventapp.mapper.EventMapper;
import jakarta.validation.constraints.Positive;
import com.college.eventapp.service.RecentlyViewedEventService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/users/{userId}/recently-viewed")
public class RecentlyViewedEventController {

    private static final int DEFAULT_LIMIT = 20;

    private final RecentlyViewedEventService recentlyViewedEventService;
    private final EventMapper eventMapper;

    public RecentlyViewedEventController(RecentlyViewedEventService recentlyViewedEventService,
                                         EventMapper eventMapper) {
        this.recentlyViewedEventService = recentlyViewedEventService;
        this.eventMapper = eventMapper;
    }

    @PostMapping("/{eventId}")
    public MessageResponseDTO recordView(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                         @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        recentlyViewedEventService.recordView(userId, eventId);
        return new MessageResponseDTO("Recently viewed event recorded successfully");
    }

    @GetMapping
    public List<EventResponseDTO> getRecentlyViewedEvents(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                                          @RequestParam(defaultValue = "20") @Positive(message = "Limit must be positive") int limit) {
        int safeLimit = limit > 0 ? limit : DEFAULT_LIMIT;
        return recentlyViewedEventService.getRecentlyViewedEvents(userId, safeLimit)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }
}
