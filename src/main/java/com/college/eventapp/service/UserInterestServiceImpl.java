package com.college.eventapp.service;

import com.college.eventapp.exception.BadRequestException;
import com.college.eventapp.exception.ResourceNotFoundException;
import com.college.eventapp.model.Role;
import com.college.eventapp.model.User;
import com.college.eventapp.model.UserInterest;
import com.college.eventapp.repository.UserInterestRepository;
import com.college.eventapp.repository.UserRepository;
import com.college.eventapp.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

@Service
public class UserInterestServiceImpl implements UserInterestService {

    private final UserRepository userRepository;
    private final UserInterestRepository userInterestRepository;
    private final CurrentUserService currentUserService;

    public UserInterestServiceImpl(UserRepository userRepository,
                                   UserInterestRepository userInterestRepository,
                                   CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.userInterestRepository = userInterestRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserInterests(Long userId) {
        User user = getStudentUser(userId);
        return userInterestRepository.findByUserOrderByInterestAsc(user)
                .stream()
                .map(UserInterest::getInterest)
                .toList();
    }

    @Override
    @Transactional
    public List<String> replaceUserInterests(Long userId, List<String> interests) {
        User user = getStudentUser(userId);
        List<String> normalizedInterests = normalizeInterests(interests);
        LocalDateTime now = LocalDateTime.now();

        userInterestRepository.deleteByUser(user);

        if (normalizedInterests.isEmpty()) {
            return normalizedInterests;
        }

        List<UserInterest> userInterests = normalizedInterests.stream()
                .map(interest -> {
                    UserInterest userInterest = new UserInterest();
                    userInterest.setUser(user);
                    userInterest.setInterest(interest);
                    userInterest.setCreatedAt(now);
                    return userInterest;
                })
                .toList();

        userInterestRepository.saveAll(userInterests);
        return normalizedInterests;
    }

    private List<String> normalizeInterests(List<String> interests) {
        if (interests == null) {
            throw new BadRequestException("Interests are required");
        }

        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String interest : interests) {
            if (interest == null) {
                throw new BadRequestException("Interest cannot be blank");
            }

            String cleaned = interest.trim().replaceAll("\\s+", " ");
            if (cleaned.isBlank()) {
                throw new BadRequestException("Interest cannot be blank");
            }

            normalized.add(toTitleCase(cleaned));
        }

        return List.copyOf(normalized);
    }

    private String toTitleCase(String value) {
        String[] parts = value.toLowerCase(Locale.ROOT).split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1));
        }
        return builder.toString();
    }

    private User getStudentUser(Long userId) {
        currentUserService.requireSameUser(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only student users can manage interests");
        }
        return user;
    }
}
