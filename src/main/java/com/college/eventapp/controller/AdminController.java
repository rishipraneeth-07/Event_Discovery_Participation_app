package com.college.eventapp.controller;

import com.college.eventapp.dto.AdminStatsDTO;
import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.dto.ModerationDecisionRequestDTO;
import com.college.eventapp.mapper.EventMapper;
import com.college.eventapp.model.Event;
import com.college.eventapp.model.EventStatus;
import com.college.eventapp.model.Role;
import com.college.eventapp.repository.EventRepository;
import com.college.eventapp.repository.RegistrationRepository;
import com.college.eventapp.repository.UserRepository;
import com.college.eventapp.service.EventService;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/admin")
public class AdminController {

    private final EventService eventService;
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    public AdminController(EventService eventService,
                           RegistrationRepository registrationRepository,
                           EventRepository eventRepository,
                           UserRepository userRepository,
                           EventMapper eventMapper) {
        this.eventService = eventService;
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }

    @GetMapping("/stats")
    public AdminStatsDTO getStats() {
        AdminStatsDTO stats = new AdminStatsDTO();
        stats.setTotalEvents(eventRepository.count());
        stats.setPendingEvents(eventRepository.findByStatus(EventStatus.PENDING).size());
        stats.setApprovedEvents(eventRepository.findByStatus(EventStatus.APPROVED).size());
        stats.setRejectedEvents(eventRepository.findByStatus(EventStatus.REJECTED).size());
        stats.setTotalUsers(userRepository.count());
        stats.setTotalStudents(userRepository.findByRole(Role.STUDENT).size());
        stats.setTotalOrganizers(userRepository.findByRole(Role.ORGANIZER).size());
        stats.setTotalRegistrations(registrationRepository.count());
        return stats;
    }

    @PutMapping("/events/{id}/approve")
    public EventResponseDTO approveEventLegacy(@PathVariable @Positive(message = "Event id must be positive") Long id,
                                               @RequestBody(required = false) ModerationDecisionRequestDTO request) {
        Event event = eventService.approveEvent(id, request != null ? request.getNote() : null);
        return eventMapper.toDTO(event);
    }

    @PutMapping("/events/{id}/reject")
    public EventResponseDTO rejectEventLegacy(@PathVariable @Positive(message = "Event id must be positive") Long id,
                                              @RequestBody(required = false) ModerationDecisionRequestDTO request) {
        Event event = eventService.rejectEvent(id, request != null ? request.getNote() : null);
        return eventMapper.toDTO(event);
    }
}
