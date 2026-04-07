package com.college.eventapp.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import com.college.eventapp.service.UserInterestService;
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
@RequestMapping("/api/users/{userId}")
public class UserPreferenceController {

    private final UserInterestService userInterestService;

    public UserPreferenceController(UserInterestService userInterestService) {
        this.userInterestService = userInterestService;
    }

    @GetMapping("/interests")
    public List<String> getInterests(@PathVariable @Positive(message = "User id must be positive") Long userId) {
        return userInterestService.getUserInterests(userId);
    }

    @PutMapping("/interests")
    public List<String> updateInterests(
            @PathVariable @Positive(message = "User id must be positive") Long userId,
            @Valid @RequestBody List<@NotBlank(message = "Interest cannot be blank") @Size(max = 100, message = "Interest must be at most 100 characters") String> interests) {
        return userInterestService.replaceUserInterests(userId, interests);
    }
}
