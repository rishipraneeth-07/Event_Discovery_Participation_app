package com.college.eventapp.controller;

import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.dto.MessageResponseDTO;
import com.college.eventapp.mapper.EventMapper;
import jakarta.validation.constraints.Positive;
import com.college.eventapp.security.CurrentUserService;
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
@RequestMapping("/api")
public class RecentlyViewedEventController {

    private static final int DEFAULT_LIMIT = 20;

    private final RecentlyViewedEventService recentlyViewedEventService;
    private final EventMapper eventMapper;
    private final CurrentUserService currentUserService;

    public RecentlyViewedEventController(RecentlyViewedEventService recentlyViewedEventService,
                                         EventMapper eventMapper,
                                         CurrentUserService currentUserService) {
        this.recentlyViewedEventService = recentlyViewedEventService;
        this.eventMapper = eventMapper;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/recently-viewed/{eventId}")
    public MessageResponseDTO recordCurrentUserView(@PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        recentlyViewedEventService.recordView(currentUserService.getCurrentUser().id(), eventId);
        return new MessageResponseDTO("Tracked");
    }

    @GetMapping("/recently-viewed")
    public List<EventResponseDTO> getCurrentUserRecentlyViewedEvents() {
        return recentlyViewedEventService.getRecentlyViewedEvents(currentUserService.getCurrentUser().id(), DEFAULT_LIMIT)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    @PostMapping("/users/{userId}/recently-viewed/{eventId}")
    public MessageResponseDTO recordViewLegacy(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                               @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        recentlyViewedEventService.recordView(userId, eventId);
        return new MessageResponseDTO("Recently viewed event recorded successfully");
    }

    @GetMapping("/users/{userId}/recently-viewed")
    public List<EventResponseDTO> getRecentlyViewedEventsLegacy(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                                                @RequestParam(defaultValue = "20") @Positive(message = "Limit must be positive") int limit) {
        int safeLimit = limit > 0 ? limit : DEFAULT_LIMIT;
        return recentlyViewedEventService.getRecentlyViewedEvents(userId, safeLimit)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }
}
