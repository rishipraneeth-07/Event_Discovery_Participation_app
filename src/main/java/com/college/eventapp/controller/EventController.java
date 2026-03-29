package com.college.eventapp.controller;

import com.college.eventapp.dto.CreateEventRequestDTO;
import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.dto.PagedResponseDTO;
import com.college.eventapp.model.Event;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.RegistrationRepository;
import com.college.eventapp.repository.UserRepository;
import com.college.eventapp.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    public EventController(EventService eventService,
                           UserRepository userRepository,
                           RegistrationRepository registrationRepository) {
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    // POST /api/events
    @PostMapping
    public EventResponseDTO createEvent(@RequestBody CreateEventRequestDTO request) {
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
    public EventResponseDTO updateEvent(@PathVariable Long id,
                                        @RequestBody CreateEventRequestDTO request) {
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
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "Event deleted successfully";
    }

    // GET /api/events/{id}
    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable Long id) {
        return convertToDTO(eventService.getEventById(id));
    }

    // GET /api/events
    // Without params  → returns all events (no pagination)
    // With params     → GET /api/events?page=0&size=10&sortBy=eventDateTime&sortDir=asc
    @GetMapping
    public Object getAllEvents(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        // If no page/size params → return plain list (keeps Android working as before)
        if (page == null || size == null) {
            return eventService.getAllEvents()
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
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
            @RequestParam String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
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
    public List<EventResponseDTO> getEventsByOrganizer(@PathVariable Long organizerId) {
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
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setCategory(event.getCategory() != null ? event.getCategory() : "General");
        dto.setCapacity(event.getCapacity() != null ? event.getCapacity() : 0);
        dto.setStatus(event.getStatus());
        dto.setOrganizerId(event.getOrganizer().getId());
        dto.setOrganizerName(event.getOrganizer().getName());

        if (event.getEventDateTime() != null) {
            dto.setDate(event.getEventDateTime().toLocalDate().toString());
            dto.setTime(event.getEventDateTime().toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            dto.setDate("");
            dto.setTime("");
        }

        dto.setRegisteredCount(registrationRepository.findByEvent(event).size());
        return dto;
    }

    private LocalDateTime parseDateTime(String date, String time) {
        try {
            String t = (time != null && !time.isBlank()) ? time : "00:00";
            return LocalDateTime.parse(date + "T" + t + ":00");
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}