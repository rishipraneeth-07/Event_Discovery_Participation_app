package com.college.eventapp.controller;

import com.college.eventapp.dto.CreateEventRequestDTO;
import com.college.eventapp.dto.EventResponseDTO;
import com.college.eventapp.model.Event;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.RegistrationRepository;
import com.college.eventapp.repository.UserRepository;
import com.college.eventapp.service.EventService;
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

    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "Event deleted successfully";
    }

    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable Long id) {
        return convertToDTO(eventService.getEventById(id));
    }

    @GetMapping
    public List<EventResponseDTO> getAllEvents() {
        return eventService.getAllEvents()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ NEW - Get events by organizer
    @GetMapping("/organizer/{organizerId}")
    public List<EventResponseDTO> getEventsByOrganizer(@PathVariable Long organizerId) {
        return eventService.getEventsByOrganizer(organizerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ NEW - Search events by keyword
    @GetMapping("/search")
    public List<EventResponseDTO> searchEvents(@RequestParam String keyword) {
        return eventService.getAllEvents()
                .stream()
                .filter(e -> e.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || e.getDescription().toLowerCase().contains(keyword.toLowerCase())
                        || e.getLocation().toLowerCase().contains(keyword.toLowerCase()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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