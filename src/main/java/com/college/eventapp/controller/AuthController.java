package com.college.eventapp.controller;

import com.college.eventapp.dto.AuthRequestDTO;
import com.college.eventapp.dto.AuthResponseDTO;
import com.college.eventapp.model.User;
import com.college.eventapp.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthResponseDTO register(@RequestBody AuthRequestDTO request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        User saved = userService.registerUser(user);
        return buildAuthResponse(saved);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequestDTO request) {
        User user = userService.loginUser(request.getEmail(), request.getPassword());
        return buildAuthResponse(user);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken("user-" + user.getId());
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
    }
}