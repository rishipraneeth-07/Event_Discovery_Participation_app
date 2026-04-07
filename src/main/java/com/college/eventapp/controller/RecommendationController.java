package com.college.eventapp.controller;

import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.mapper.EventMapper;
import jakarta.validation.constraints.Positive;
import com.college.eventapp.service.RecommendationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/users/{userId}")
public class RecommendationController {

    private static final int DEFAULT_LIMIT = 10;

    private final RecommendationService recommendationService;
    private final EventMapper eventMapper;

    public RecommendationController(RecommendationService recommendationService,
                                    EventMapper eventMapper) {
        this.recommendationService = recommendationService;
        this.eventMapper = eventMapper;
    }

    @GetMapping("/recommended-events")
    public List<EventResponseDTO> getRecommendedEvents(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                                       @RequestParam(defaultValue = "10") @Positive(message = "Limit must be positive") int limit) {
        int safeLimit = limit > 0 ? limit : DEFAULT_LIMIT;
        return recommendationService.getRecommendedEvents(userId, safeLimit)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }
}
