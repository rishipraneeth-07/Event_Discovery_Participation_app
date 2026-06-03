package com.college.eventapp.service;

import com.college.eventapp.exception.BadRequestException;
import com.college.eventapp.exception.ResourceNotFoundException;
import com.college.eventapp.model.Event;
import com.college.eventapp.model.EventStatus;
import com.college.eventapp.model.RecentlyViewedEvent;
import com.college.eventapp.model.Role;
import com.college.eventapp.model.SavedEvent;
import com.college.eventapp.model.User;
import com.college.eventapp.model.UserInterest;
import com.college.eventapp.repository.EventRepository;
import com.college.eventapp.repository.RecentlyViewedEventRepository;
import com.college.eventapp.repository.RegistrationRepository;
import com.college.eventapp.repository.SavedEventRepository;
import com.college.eventapp.repository.UserInterestRepository;
import com.college.eventapp.repository.UserRepository;
import com.college.eventapp.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private static final int TRENDING_THRESHOLD = 5;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserInterestRepository userInterestRepository;
    private final SavedEventRepository savedEventRepository;
    private final RecentlyViewedEventRepository recentlyViewedEventRepository;
    private final RegistrationRepository registrationRepository;
    private final CurrentUserService currentUserService;

    public RecommendationServiceImpl(UserRepository userRepository,
                                     EventRepository eventRepository,
                                     UserInterestRepository userInterestRepository,
                                     SavedEventRepository savedEventRepository,
                                     RecentlyViewedEventRepository recentlyViewedEventRepository,
                                     RegistrationRepository registrationRepository,
                                     CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.userInterestRepository = userInterestRepository;
        this.savedEventRepository = savedEventRepository;
        this.recentlyViewedEventRepository = recentlyViewedEventRepository;
        this.registrationRepository = registrationRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getRecommendedEvents(Long userId, int limit) {
        if (limit <= 0) {
            throw new BadRequestException("Limit must be greater than zero");
        }

        User user = getStudentUser(userId);
        List<Event> candidateEvents = eventRepository.findByStatusAndEventDateTimeAfter(
                EventStatus.APPROVED,
                LocalDateTime.now()
        );

        if (candidateEvents.isEmpty()) {
            return List.of();
        }

        Set<String> interestCategories = userInterestRepository.findByUserOrderByInterestAsc(user)
                .stream()
                .map(UserInterest::getInterest)
                .map(this::normalizeCategory)
                .collect(java.util.stream.Collectors.toSet());

        List<SavedEvent> savedEvents = savedEventRepository.findByUserOrderByCreatedAtDesc(user);
        Set<String> savedCategories = savedEvents.stream()
                .map(SavedEvent::getEvent)
                .map(Event::getCategory)
                .map(this::normalizeCategory)
                .filter(category -> !category.isBlank())
                .collect(java.util.stream.Collectors.toSet());

        Set<Long> savedEventIds = new HashSet<>();
        savedEvents.forEach(savedEvent -> savedEventIds.add(savedEvent.getEvent().getId()));

        Set<String> recentlyViewedCategories = recentlyViewedEventRepository.findByUserOrderByViewedAtDesc(user)
                .stream()
                .map(RecentlyViewedEvent::getEvent)
                .map(Event::getCategory)
                .map(this::normalizeCategory)
                .filter(category -> !category.isBlank())
                .collect(java.util.stream.Collectors.toSet());

        Map<Long, Integer> registrationCounts = new HashMap<>();
        candidateEvents.forEach(event -> registrationCounts.put(event.getId(), (int) registrationRepository.countByEvent(event)));

        boolean hasPreferences = !interestCategories.isEmpty()
                || !savedCategories.isEmpty()
                || !recentlyViewedCategories.isEmpty();

        if (!hasPreferences) {
            return sortTrending(candidateEvents, registrationCounts, limit);
        }

        List<Event> recommendedEvents = candidateEvents.stream()
                .filter(event -> !savedEventIds.contains(event.getId()))
                .sorted(Comparator
                        .comparingInt((Event event) -> scoreEvent(
                                event,
                                interestCategories,
                                savedCategories,
                                recentlyViewedCategories,
                                registrationCounts.getOrDefault(event.getId(), 0)))
                        .reversed()
                        .thenComparing((Event event) -> registrationCounts.getOrDefault(event.getId(), 0), Comparator.reverseOrder())
                        .thenComparing(Event::getEventDateTime))
                .limit(limit)
                .toList();

        if (recommendedEvents.isEmpty()) {
            return sortTrending(candidateEvents, registrationCounts, limit);
        }

        return recommendedEvents;
    }

    private List<Event> sortTrending(List<Event> events, Map<Long, Integer> registrationCounts, int limit) {
        return events.stream()
                .sorted(Comparator
                        .comparing((Event event) -> registrationCounts.getOrDefault(event.getId(), 0), Comparator.reverseOrder())
                        .thenComparing(Event::getEventDateTime))
                .limit(limit)
                .toList();
    }

    private int scoreEvent(Event event,
                           Set<String> interestCategories,
                           Set<String> savedCategories,
                           Set<String> recentlyViewedCategories,
                           int registeredCount) {
        int score = 0;
        String category = normalizeCategory(event.getCategory());

        if (!category.isBlank() && interestCategories.contains(category)) {
            score += 5;
        }
        if (!category.isBlank() && savedCategories.contains(category)) {
            score += 3;
        }
        if (!category.isBlank() && recentlyViewedCategories.contains(category)) {
            score += 2;
        }
        if (registeredCount >= TRENDING_THRESHOLD) {
            score += 1;
        }

        return score;
    }

    private String normalizeCategory(String category) {
        return category == null ? "" : category.trim().toLowerCase(Locale.ROOT);
    }

    private User getStudentUser(Long userId) {
        currentUserService.requireSameUser(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only student users can fetch recommendations");
        }
        return user;
    }
}
