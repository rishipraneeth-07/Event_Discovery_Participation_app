package com.college.eventapp.controller;

import com.college.eventapp.model.Event;
import com.college.eventapp.model.Registration;
import com.college.eventapp.model.User;
import com.college.eventapp.service.EventService;
import com.college.eventapp.service.RegistrationService;
import com.college.eventapp.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/events/{eventId}/register")
    public Registration registerForEvent(@PathVariable Long eventId,
                                         @RequestParam Long userId) {
        return registrationService.registerForEvent(userId, eventId);
    }

    @GetMapping("/users/{userId}/registrations")
    public List<Registration> getUserRegistrations(@PathVariable Long userId) {
        return registrationService.getUserRegistrations(userId);
    }

    @GetMapping("/events/{eventId}/registrations")
    public List<Registration> getEventRegistrations(@PathVariable Long eventId) {
        return registrationService.getEventRegistrations(eventId);
    }
}
