package com.college.eventapp.controller;

import com.college.eventapp.dto.CreateEventRequestDTO;
import com.college.eventapp.dto.EventAnalyticsResponseDTO;
import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.dto.MessageResponseDTO;
import com.college.eventapp.dto.ModerationDecisionRequestDTO;
import com.college.eventapp.dto.PagedResponseDTO;
import com.college.eventapp.exception.BadRequestException;
import com.college.eventapp.mapper.EventMapper;
import com.college.eventapp.model.Event;
import com.college.eventapp.model.EventStatus;
import com.college.eventapp.model.Role;
import com.college.eventapp.model.User;
import com.college.eventapp.security.CurrentUserService;
import com.college.eventapp.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final CurrentUserService currentUserService;

    public EventController(EventService eventService,
                           EventMapper eventMapper,
                           CurrentUserService currentUserService) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDTO createEvent(@Valid @RequestBody CreateEventRequestDTO request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setCategory(request.getCategory());
        event.setCapacity(request.getCapacity() != null ? request.getCapacity() : 0);
        event.setEventDateTime(parseDateTime(request.getDate(), request.getTime()));

        User organizer = new User();
        organizer.setId(request.getOrganizerId());
        event.setOrganizer(organizer);

        return convertToDTO(eventService.createEvent(event));
    }

    @PutMapping("/{id}")
    public EventResponseDTO updateEvent(@PathVariable @Positive(message = "Event id must be positive") Long id,
                                        @Valid @RequestBody CreateEventRequestDTO request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setCategory(request.getCategory());
        event.setCapacity(request.getCapacity() != null ? request.getCapacity() : 0);
        event.setEventDateTime(parseDateTime(request.getDate(), request.getTime()));

        return convertToDTO(eventService.updateEvent(id, event));
    }

    @DeleteMapping("/{id}")
    public MessageResponseDTO deleteEvent(@PathVariable @Positive(message = "Event id must be positive") Long id) {
        eventService.deleteEvent(id);
        return new MessageResponseDTO("Event deleted successfully");
    }

    @GetMapping("/{id}/analytics")
    public EventAnalyticsResponseDTO getEventAnalytics(@PathVariable @Positive(message = "Event id must be positive") Long id) {
        return eventService.getEventAnalytics(id);
    }

    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable @Positive(message = "Event id must be positive") Long id) {
        return convertToDTO(eventService.getEventById(id));
    }

    @GetMapping
    public PagedResponseDTO<EventResponseDTO> getEvents(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        EventStatus requestedStatus = resolveRequestedStatus(status);
        int pageNumber = page != null ? Math.max(page, 0) : 0;
        int pageSize = size != null ? Math.max(size, 1) : 20;

        List<Event> events;
        if (requestedStatus == null || requestedStatus == EventStatus.APPROVED) {
            events = eventService.getApprovedEvents();
        } else {
            currentUserService.requireRole(Role.ADMIN, Role.ORGANIZER);
            events = eventService.getAllEvents().stream()
                    .filter(event -> event.getStatus() == requestedStatus)
                    .toList();
        }

        List<Event> filteredEvents = events.stream()
                .filter(event -> category == null || category.isBlank() || categoryMatches(event, category))
                .toList();
        return toPagedResponse(filteredEvents, pageNumber, pageSize);
    }

    @GetMapping("/search")
    public PagedResponseDTO<EventResponseDTO> searchEvents(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int pageNumber = page != null ? Math.max(page, 0) : 0;
        int pageSize = size != null ? Math.max(size, 1) : 20;
        String searchTerm = query;
        if (searchTerm == null || searchTerm.isBlank()) {
            searchTerm = keyword;
        }
        if (searchTerm == null || searchTerm.isBlank()) {
            throw new BadRequestException("Query is required");
        }

        String loweredSearch = searchTerm.toLowerCase();
        List<Event> searchSource = currentUserService.hasRole(Role.ADMIN) || currentUserService.hasRole(Role.ORGANIZER)
                ? eventService.getAllEvents()
                : eventService.getApprovedEvents();
        List<Event> filteredEvents = searchSource.stream()
                .filter(event -> containsIgnoreCase(event.getTitle(), loweredSearch)
                        || containsIgnoreCase(event.getDescription(), loweredSearch)
                        || containsIgnoreCase(event.getLocation(), loweredSearch))
                .filter(event -> category == null || category.isBlank() || categoryMatches(event, category))
                .toList();
        return toPagedResponse(filteredEvents, pageNumber, pageSize);
    }

    @GetMapping("/organizer")
    public List<EventResponseDTO> getCurrentOrganizerEvents() {
        currentUserService.requireRole(Role.ORGANIZER, Role.ADMIN);
        return eventService.getEventsByOrganizer(currentUserService.getCurrentUser().id())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @GetMapping("/organizer/{organizerId}")
    public List<EventResponseDTO> getEventsByOrganizerLegacy(
            @PathVariable @Positive(message = "Organizer id must be positive") Long organizerId) {
        return eventService.getEventsByOrganizer(organizerId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @GetMapping("/pending")
    public List<EventResponseDTO> getPendingEvents() {
        currentUserService.requireRole(Role.ADMIN);
        return eventService.getAllEvents().stream()
                .filter(event -> event.getStatus() == EventStatus.PENDING)
                .map(this::convertToDTO)
                .toList();
    }

    @PostMapping("/{id}/approve")
    public EventResponseDTO approveEvent(@PathVariable @Positive(message = "Event id must be positive") Long id,
                                         @RequestBody(required = false) ModerationDecisionRequestDTO request) {
        return convertToDTO(eventService.approveEvent(id, request != null ? request.getNote() : null));
    }

    @PostMapping("/{id}/reject")
    public EventResponseDTO rejectEvent(@PathVariable @Positive(message = "Event id must be positive") Long id,
                                        @RequestBody(required = false) ModerationDecisionRequestDTO request) {
        return convertToDTO(eventService.rejectEvent(id, request != null ? request.getNote() : null));
    }

    @PutMapping("/{id}/cancel")
    public MessageResponseDTO cancelEvent(@PathVariable @Positive(message = "Event id must be positive") Long id) {
        eventService.cancelEvent(id);
        return new MessageResponseDTO("Event cancelled successfully");
    }

    @PostMapping("/{id}/duplicate")
    public EventResponseDTO duplicateEvent(@PathVariable @Positive(message = "Event id must be positive") Long id) {
        return convertToDTO(eventService.duplicateEvent(id));
    }

    private EventResponseDTO convertToDTO(Event event) {
        return eventMapper.toDTO(event);
    }

    private LocalDateTime parseDateTime(String date, String time) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            LocalTime parsedTime = (time != null && !time.isBlank())
                    ? LocalTime.parse(time)
                    : LocalTime.MIDNIGHT;
            return parsedDate.atTime(parsedTime);
        } catch (Exception e) {
            throw new BadRequestException("Date or time format is invalid");
        }
    }

    private EventStatus resolveRequestedStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return EventStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Unsupported event status: " + status);
        }
    }

    private boolean containsIgnoreCase(String source, String search) {
        return source != null && source.toLowerCase().contains(search);
    }

    private boolean categoryMatches(Event event, String category) {
        return event.getCategory() != null && event.getCategory().toLowerCase().contains(category.trim().toLowerCase());
    }

    private PagedResponseDTO<EventResponseDTO> toPagedResponse(List<Event> events, int pageNumber, int pageSize) {
        int fromIndex = Math.min(pageNumber * pageSize, events.size());
        int toIndex = Math.min(fromIndex + pageSize, events.size());
        PageImpl<Event> page = new PageImpl<>(
                events.subList(fromIndex, toIndex),
                PageRequest.of(pageNumber, pageSize),
                events.size()
        );

        PagedResponseDTO<EventResponseDTO> response = new PagedResponseDTO<>();
        response.setContent(page.getContent().stream().map(this::convertToDTO).toList());
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}
