package com.college.eventapp.controller;

import com.college.eventapp.dto.CreateEventRequestDTO;
import com.college.eventapp.dto.EventAnalyticsResponseDTO;
import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.dto.MessageResponseDTO;
import com.college.eventapp.dto.PagedResponseDTO;
import com.college.eventapp.exception.BadRequestException;
import com.college.eventapp.mapper.EventMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import com.college.eventapp.model.Event;
import com.college.eventapp.model.User;
import com.college.eventapp.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService,
                           EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    // POST /api/events
    @PostMapping
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

    // PUT /api/events/{id}
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

    // DELETE /api/events/{id}
    @DeleteMapping("/{id}")
    public MessageResponseDTO deleteEvent(@PathVariable @Positive(message = "Event id must be positive") Long id) {
        eventService.deleteEvent(id);
        return new MessageResponseDTO("Event deleted successfully");
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

    @GetMapping("/{id}/analytics")
    public EventAnalyticsResponseDTO getEventAnalytics(@PathVariable @Positive(message = "Event id must be positive") Long id) {
        return eventService.getEventAnalytics(id);
    }

    // GET /api/events/{id}
    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable @Positive(message = "Event id must be positive") Long id) {
        return convertToDTO(eventService.getEventById(id));
    }

    @GetMapping
    public List<EventResponseDTO> getHomeFeedEvents() {
        return eventService.getAllEvents()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // GET /api/events
    // Without params  → returns all events (no pagination)
    // With params     → GET /api/events?page=0&size=10&sortBy=eventDateTime&sortDir=asc
    @GetMapping("/paged")
    public PagedResponseDTO<EventResponseDTO> getAllEvents(
            @RequestParam @Min(value = 0, message = "Page must be 0 or greater") Integer page,
            @RequestParam @Positive(message = "Size must be positive") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        // If no page/size params → return plain list (keeps Android working as before)
        if (page == null || size == null) {
            throw new BadRequestException("Page and size are required for paged results");
        }

        // With page/size → return paginated response
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Event> eventPage = eventService.getAllEventsPaged(pageable);
        return buildPagedResponse(eventPage);
    }

    // GET /api/events/search?keyword=music&page=0&size=10
    @GetMapping("/search")
    public Object searchEvents(
            @RequestParam @NotBlank(message = "Keyword is required") String keyword,
            @RequestParam(required = false) @Min(value = 0, message = "Page must be 0 or greater") Integer page,
            @RequestParam(required = false) @Positive(message = "Size must be positive") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        if (page == null || size == null) {
            return eventService.getAllEvents()
                    .stream()
                    .filter(e -> e.getTitle().toLowerCase().contains(keyword.toLowerCase())
                            || e.getDescription().toLowerCase().contains(keyword.toLowerCase())
                            || e.getLocation().toLowerCase().contains(keyword.toLowerCase()))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Event> eventPage = eventService.searchEventsPaged(keyword, pageable);
        return buildPagedResponse(eventPage);
    }

    // GET /api/events/organizer/{organizerId}
    @GetMapping("/organizer/{organizerId}")
    public List<EventResponseDTO> getEventsByOrganizer(@PathVariable @Positive(message = "Organizer id must be positive") Long organizerId) {
        return eventService.getEventsByOrganizer(organizerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private PagedResponseDTO<EventResponseDTO> buildPagedResponse(Page<Event> eventPage) {
        PagedResponseDTO<EventResponseDTO> response = new PagedResponseDTO<>();
        response.setContent(eventPage.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        response.setPage(eventPage.getNumber());
        response.setSize(eventPage.getSize());
        response.setTotalElements(eventPage.getTotalElements());
        response.setTotalPages(eventPage.getTotalPages());
        response.setFirst(eventPage.isFirst());
        response.setLast(eventPage.isLast());
        response.setHasNext(eventPage.hasNext());
        response.setHasPrevious(eventPage.hasPrevious());
        return response;
    }

    private EventResponseDTO convertToDTO(Event event) {
        return eventMapper.toDTO(event);
    }

    private LocalDateTime parseDateTime(String date, String time) {
        try {
            String t = (time != null && !time.isBlank()) ? time : "00:00";
            return LocalDateTime.parse(date + "T" + t + ":00");
        } catch (Exception e) {
            throw new BadRequestException("Date or time format is invalid");
        }
    }
}
