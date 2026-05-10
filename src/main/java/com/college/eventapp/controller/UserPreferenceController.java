package com.college.eventapp.controller;

import com.college.eventapp.dto.InterestsUpdateRequestDTO;
import com.college.eventapp.dto.MessageResponseDTO;
import com.college.eventapp.security.CurrentUserService;
import com.college.eventapp.service.UserInterestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api")
public class UserPreferenceController {

    private final UserInterestService userInterestService;
    private final CurrentUserService currentUserService;

    public UserPreferenceController(UserInterestService userInterestService,
                                    CurrentUserService currentUserService) {
        this.userInterestService = userInterestService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/users/me/interests")
    public List<String> getCurrentUserInterests() {
        return userInterestService.getUserInterests(currentUserService.getCurrentUser().id());
    }

    @PutMapping("/users/me/interests")
    public MessageResponseDTO updateCurrentUserInterests(
            @Valid @RequestBody InterestsUpdateRequestDTO request) {
        userInterestService.replaceUserInterests(currentUserService.getCurrentUser().id(), request.getInterests());
        return new MessageResponseDTO("Interests updated successfully");
    }

    @GetMapping("/users/{userId}/interests")
    public List<String> getInterestsLegacy(@PathVariable @Positive(message = "User id must be positive") Long userId) {
        return userInterestService.getUserInterests(userId);
    }

    @PutMapping("/users/{userId}/interests")
    public List<String> updateInterestsLegacy(
            @PathVariable @Positive(message = "User id must be positive") Long userId,
            @Valid @RequestBody List<@NotBlank(message = "Interest cannot be blank") @Size(max = 100, message = "Interest must be at most 100 characters") String> interests) {
        return userInterestService.replaceUserInterests(userId, interests);
    }
}
