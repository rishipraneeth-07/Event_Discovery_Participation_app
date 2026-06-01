package com.college.eventapp.service;

import com.college.eventapp.dto.EventAnalyticsResponseDTO;
import com.college.eventapp.exception.BadRequestException;
import com.college.eventapp.exception.ResourceNotFoundException;
import com.college.eventapp.model.Event;
import com.college.eventapp.model.EventModerationLog;
import com.college.eventapp.model.EventStatus;
import com.college.eventapp.model.Registration;
import com.college.eventapp.model.Role;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.EventModerationLogRepository;
import com.college.eventapp.repository.EventRepository;
import com.college.eventapp.repository.NotificationRepository;
import com.college.eventapp.repository.RecentlyViewedEventRepository;
import com.college.eventapp.repository.RegistrationRepository;
import com.college.eventapp.repository.SavedEventRepository;
import com.college.eventapp.repository.UserRepository;
import com.college.eventapp.security.AuthenticatedUser;
import com.college.eventapp.security.CurrentUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final SavedEventRepository savedEventRepository;
    private final RecentlyViewedEventRepository recentlyViewedEventRepository;
    private final NotificationRepository notificationRepository;
    private final EventModerationLogRepository eventModerationLogRepository;
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public EventServiceImpl(EventRepository eventRepository,
                            UserRepository userRepository,
                            RegistrationRepository registrationRepository,
                            SavedEventRepository savedEventRepository,
                            RecentlyViewedEventRepository recentlyViewedEventRepository,
                            NotificationRepository notificationRepository,
                            EventModerationLogRepository eventModerationLogRepository,
                            NotificationService notificationService,
                            CurrentUserService currentUserService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
        this.savedEventRepository = savedEventRepository;
        this.recentlyViewedEventRepository = recentlyViewedEventRepository;
        this.notificationRepository = notificationRepository;
        this.eventModerationLogRepository = eventModerationLogRepository;
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public Event createEvent(Event event) {
        currentUserService.requireRole(Role.ORGANIZER, Role.ADMIN);
        requireFutureEventDateTime(event.getEventDateTime());

        Long organizerId = event.getOrganizer().getId();
        currentUserService.requireSameUserOrAdmin(organizerId);

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found"));
        if (organizer.getRole() != Role.ORGANIZER) {
            throw new BadRequestException("Only organizer accounts can create events");
        }

        event.setOrganizer(organizer);
        event.setStatus(EventStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();
        event.setCreatedAt(now);
        event.setUpdatedAt(now);
        event.setUpdatedBy(currentUserService.getCurrentUser().id());
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event updateEvent(Long id, Event updatedEvent) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        currentUserService.requireSameUserOrAdmin(event.getOrganizer().getId());
        requireFutureEventDateTime(updatedEvent.getEventDateTime());
        boolean wasApproved = event.getStatus() == EventStatus.APPROVED;

        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setLocation(updatedEvent.getLocation());
        event.setEventDateTime(updatedEvent.getEventDateTime());

        if (updatedEvent.getCategory() != null) {
            event.setCategory(updatedEvent.getCategory());
        }
        if (updatedEvent.getCapacity() != null) {
            event.setCapacity(updatedEvent.getCapacity());
        }
        if (wasApproved) {
            event.setStatus(EventStatus.PENDING);
        }
        event.setUpdatedAt(LocalDateTime.now());
        event.setUpdatedBy(currentUserService.getCurrentUser().id());
        Event saved = eventRepository.save(event);
        if (wasApproved) {
            notificationService.createMandatoryNotification(
                    saved.getOrganizer().getId(),
                    "Your event \"" + saved.getTitle() + "\" was updated and sent back for admin review.",
                    "APPROVAL"
            );
        }

        return saved;
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        currentUserService.requireSameUserOrAdmin(event.getOrganizer().getId());
        registrationRepository.deleteByEvent(event);
        savedEventRepository.deleteByEvent(event);
        recentlyViewedEventRepository.deleteByEvent(event);
        eventModerationLogRepository.deleteByEvent(event);
        eventRepository.delete(event);
    }

    @Override
    @Transactional
    public Event cancelEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        currentUserService.requireSameUserOrAdmin(event.getOrganizer().getId());

        event.setStatus(EventStatus.CANCELLED);
        event.setUpdatedAt(LocalDateTime.now());
        event.setUpdatedBy(currentUserService.getCurrentUser().id());
        Event saved = eventRepository.save(event);

        for (Registration registration : registrationRepository.findByEvent(event)) {
            notificationService.createNotification(
                    registration.getUser().getId(),
                    "\"" + event.getTitle() + "\" was cancelled.",
                    "EVENT_UPDATE",
                    "registrationUpdates"
            );
        }

        return saved;
    }

    @Override
    @Transactional
    public Event duplicateEvent(Long id) {
        Event source = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        currentUserService.requireRole(Role.ORGANIZER, Role.ADMIN);
        AuthenticatedUser currentUser = currentUserService.getCurrentUser();

        User organizer;
        if (currentUser.role() == Role.ADMIN) {
            organizer = source.getOrganizer();
        } else {
            organizer = userRepository.findById(currentUser.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Organizer not found"));
        }
        if (organizer.getRole() != Role.ORGANIZER) {
            throw new BadRequestException("Duplicated events must belong to an organizer account");
        }

        Event duplicate = new Event();
        duplicate.setTitle(source.getTitle());
        duplicate.setDescription(source.getDescription());
        duplicate.setLocation(source.getLocation());
        duplicate.setEventDateTime(resolveDuplicateEventDateTime(source.getEventDateTime()));
        duplicate.setCategory(source.getCategory());
        duplicate.setCapacity(source.getCapacity());
        duplicate.setOrganizer(organizer);
        duplicate.setStatus(EventStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();
        duplicate.setCreatedAt(now);
        duplicate.setUpdatedAt(now);
        duplicate.setUpdatedBy(currentUser.id());
        return eventRepository.save(duplicate);
    }

    @Override
    @Transactional(readOnly = true)
    public EventAnalyticsResponseDTO getEventAnalytics(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        currentUserService.requireSameUserOrAdmin(event.getOrganizer().getId());

        EventAnalyticsResponseDTO dto = new EventAnalyticsResponseDTO();
        long registrations = registrationRepository.countByEvent(event);
        int capacity = event.getCapacity() != null ? event.getCapacity() : 0;
        int availableSpots = Math.max(capacity - (int) registrations, 0);
        double fillRate = capacity > 0 ? Math.min(1.0, (double) registrations / capacity) : 0.0;

        dto.setEventId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setTotalCapacity(capacity);
        dto.setRegisteredCount(registrations);
        dto.setAvailableSpots(availableSpots);
        dto.setFillRate(fillRate);
        dto.setStatus(event.getStatus().name());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (currentUserService.hasRole(Role.STUDENT) && event.getStatus() != EventStatus.APPROVED) {
            throw new ResourceNotFoundException("Event not found");
        }

        return event;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        if (currentUserService.hasRole(Role.STUDENT)) {
            return eventRepository.findByStatus(EventStatus.APPROVED);
        }
        return eventRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getApprovedEvents() {
        return eventRepository.findByStatus(EventStatus.APPROVED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByOrganizer(Long organizerId) {
        currentUserService.requireRole(Role.ORGANIZER, Role.ADMIN);
        currentUserService.requireSameUserOrAdmin(organizerId);

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found"));
        return eventRepository.findByOrganizer(organizer);
    }

    @Override
    @Transactional
    public Event approveEvent(Long eventId, String note) {
        currentUserService.requireRole(Role.ADMIN);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        if (event.getStatus() != EventStatus.PENDING) {
            throw new BadRequestException("Only pending events can be approved");
        }
        event.setStatus(EventStatus.APPROVED);
        event.setUpdatedAt(LocalDateTime.now());
        event.setUpdatedBy(currentUserService.getCurrentUser().id());
        Event saved = eventRepository.save(event);
        saveModerationLog(saved, "APPROVED", note);
        notificationService.createMandatoryNotification(
                saved.getOrganizer().getId(),
                "Your event \"" + saved.getTitle() + "\" was approved.",
                "APPROVAL"
        );
        return saved;
    }

    @Override
    @Transactional
    public Event rejectEvent(Long eventId, String note) {
        currentUserService.requireRole(Role.ADMIN);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        if (event.getStatus() != EventStatus.PENDING) {
            throw new BadRequestException("Only pending events can be rejected");
        }
        event.setStatus(EventStatus.REJECTED);
        event.setUpdatedAt(LocalDateTime.now());
        event.setUpdatedBy(currentUserService.getCurrentUser().id());
        Event saved = eventRepository.save(event);
        saveModerationLog(saved, "REJECTED", note);
        notificationService.createMandatoryNotification(
                saved.getOrganizer().getId(),
                "Your event \"" + saved.getTitle() + "\" was rejected.",
                "REJECTION"
        );
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Event> getAllEventsPaged(Pageable pageable) {
        if (currentUserService.hasRole(Role.STUDENT)) {
            return eventRepository.findByStatus(EventStatus.APPROVED, pageable);
        }
        return eventRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Event> getApprovedEventsPaged(Pageable pageable) {
        return eventRepository.findByStatus(EventStatus.APPROVED, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Event> searchEventsPaged(String keyword, Pageable pageable) {
        if (currentUserService.hasRole(Role.STUDENT)) {
            return eventRepository.searchByKeywordAndStatus(keyword, EventStatus.APPROVED, pageable);
        }

        return eventRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase(
                        keyword, keyword, keyword, pageable);
    }

    private void saveModerationLog(Event event, String action, String note) {
        AuthenticatedUser currentUser = currentUserService.getCurrentUser();
        User admin = userRepository.findById(currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        EventModerationLog log = new EventModerationLog();
        log.setEvent(event);
        log.setAdmin(admin);
        log.setAction(action);
        log.setNote(note);
        log.setCreatedAt(LocalDateTime.now());
        eventModerationLogRepository.save(log);
    }

    private void requireFutureEventDateTime(LocalDateTime eventDateTime) {
        if (eventDateTime == null) {
            throw new BadRequestException("Event date and time are required");
        }
        if (!eventDateTime.isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Event date and time must be in the future");
        }
    }

    private LocalDateTime resolveDuplicateEventDateTime(LocalDateTime sourceDateTime) {
        if (sourceDateTime == null) {
            throw new BadRequestException("Source event date and time are missing");
        }
        if (sourceDateTime.isAfter(LocalDateTime.now())) {
            return sourceDateTime;
        }

        LocalDateTime nextWeek = LocalDateTime.now().plusDays(7);
        return nextWeek
                .withHour(sourceDateTime.getHour())
                .withMinute(sourceDateTime.getMinute())
                .withSecond(sourceDateTime.getSecond())
                .withNano(sourceDateTime.getNano());
    }
}
