package com.college.eventapp.controller;

import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.dto.ExistsResponseDTO;
import com.college.eventapp.dto.MessageResponseDTO;
import com.college.eventapp.mapper.EventMapper;
import jakarta.validation.constraints.Positive;
import com.college.eventapp.service.SavedEventService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/users/{userId}/saved-events")
public class SavedEventController {

    private final SavedEventService savedEventService;
    private final EventMapper eventMapper;

    public SavedEventController(SavedEventService savedEventService,
                                EventMapper eventMapper) {
        this.savedEventService = savedEventService;
        this.eventMapper = eventMapper;
    }

    @PostMapping("/{eventId}")
    public MessageResponseDTO saveEvent(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                        @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        savedEventService.saveEvent(userId, eventId);
        return new MessageResponseDTO("Event saved successfully");
    }

    @DeleteMapping("/{eventId}")
    public MessageResponseDTO unsaveEvent(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                          @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        savedEventService.unsaveEvent(userId, eventId);
        return new MessageResponseDTO("Saved event removed successfully");
    }

    @GetMapping
    public List<EventResponseDTO> getSavedEvents(@PathVariable @Positive(message = "User id must be positive") Long userId) {
        return savedEventService.getSavedEvents(userId)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    @GetMapping("/{eventId}/exists")
    public ExistsResponseDTO isEventSaved(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                          @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        ExistsResponseDTO response = new ExistsResponseDTO();
        response.setExists(savedEventService.isEventSaved(userId, eventId));
        return response;
    }
}
