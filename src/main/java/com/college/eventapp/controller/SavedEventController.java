package com.college.eventapp.controller;

import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.dto.ExistsResponseDTO;
import com.college.eventapp.dto.MessageResponseDTO;
import com.college.eventapp.mapper.EventMapper;
import jakarta.validation.constraints.Positive;
import com.college.eventapp.security.CurrentUserService;
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
@RequestMapping("/api")
public class SavedEventController {

    private final SavedEventService savedEventService;
    private final EventMapper eventMapper;
    private final CurrentUserService currentUserService;

    public SavedEventController(SavedEventService savedEventService,
                                EventMapper eventMapper,
                                CurrentUserService currentUserService) {
        this.savedEventService = savedEventService;
        this.eventMapper = eventMapper;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/saved-events/{eventId}")
    public MessageResponseDTO saveEvent(@PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        savedEventService.saveEvent(currentUserService.getCurrentUser().id(), eventId);
        return new MessageResponseDTO("Event saved");
    }

    @DeleteMapping("/saved-events/{eventId}")
    public MessageResponseDTO unsaveEvent(@PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        savedEventService.unsaveEvent(currentUserService.getCurrentUser().id(), eventId);
        return new MessageResponseDTO("Event removed from saved");
    }

    @GetMapping("/saved-events")
    public List<EventResponseDTO> getSavedEvents() {
        return savedEventService.getSavedEvents(currentUserService.getCurrentUser().id())
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    @GetMapping("/saved-events/{eventId}/check")
    public ExistsResponseDTO isEventSaved(@PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        ExistsResponseDTO response = new ExistsResponseDTO();
        response.setExists(savedEventService.isEventSaved(currentUserService.getCurrentUser().id(), eventId));
        return response;
    }

    @PostMapping("/users/{userId}/saved-events/{eventId}")
    public MessageResponseDTO saveEventLegacy(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                              @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        savedEventService.saveEvent(userId, eventId);
        return new MessageResponseDTO("Event saved successfully");
    }

    @DeleteMapping("/users/{userId}/saved-events/{eventId}")
    public MessageResponseDTO unsaveEventLegacy(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                                @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        savedEventService.unsaveEvent(userId, eventId);
        return new MessageResponseDTO("Saved event removed successfully");
    }

    @GetMapping("/users/{userId}/saved-events")
    public List<EventResponseDTO> getSavedEventsLegacy(@PathVariable @Positive(message = "User id must be positive") Long userId) {
        return savedEventService.getSavedEvents(userId)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    @GetMapping("/users/{userId}/saved-events/{eventId}/exists")
    public ExistsResponseDTO isEventSavedLegacy(@PathVariable @Positive(message = "User id must be positive") Long userId,
                                                @PathVariable @Positive(message = "Event id must be positive") Long eventId) {
        ExistsResponseDTO response = new ExistsResponseDTO();
        response.setExists(savedEventService.isEventSaved(userId, eventId));
        return response;
    }
}
