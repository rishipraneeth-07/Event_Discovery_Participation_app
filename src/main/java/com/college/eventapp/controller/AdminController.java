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
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/admin/events")
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

    @PutMapping("/{id}/approve")
    public EventResponseDTO approveEvent(@PathVariable @Positive(message = "Event id must be positive") Long id,
                                         @RequestBody(required = false) ModerationDecisionRequestDTO request) {
        Event event = eventService.approveEvent(id, request != null ? request.getNote() : null);
        /*

        // ✅ Notify organizer
        notificationService.createNotification(
                event.getOrganizer().getId(),
                "Your event '" + event.getTitle() + "' has been approved! Students can now register.",
                "eventApproval"
        );

        // ✅ Notify all registered students
        registrationRepository.findByEvent(event).forEach(reg ->
                notificationService.createNotification(
                        reg.getUser().getId(),
                        "Good news! The event '" + event.getTitle() + "' you registered for has been approved.",
                        "registrationUpdates"
                )
        );

        */
        return convertToDTO(event);
    }

    @PutMapping("/{id}/reject")
    public EventResponseDTO rejectEvent(@PathVariable @Positive(message = "Event id must be positive") Long id,
                                        @RequestBody(required = false) ModerationDecisionRequestDTO request) {
        Event event = eventService.rejectEvent(id, request != null ? request.getNote() : null);
        /*

        // ✅ Notify organizer
        notificationService.createNotification(
                event.getOrganizer().getId(),
                "Your event '" + event.getTitle() + "' has been rejected. Please review and resubmit.",
                "eventApproval"
        );

        */
        return convertToDTO(event);
    }

    private EventResponseDTO convertToDTO(Event event) {
        return eventMapper.toDTO(event);
    }
}
