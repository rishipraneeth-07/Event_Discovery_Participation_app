package com.college.eventapp.controller;

import com.college.eventapp.dto.AuthRequestDTO;
import com.college.eventapp.dto.AuthResponseDTO;
import jakarta.validation.Valid;
import com.college.eventapp.model.User;
import com.college.eventapp.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthResponseDTO register(@Valid @RequestBody AuthRequestDTO request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        User saved = userService.registerUser(user);
        return buildAuthResponse(saved);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequest request) {
        User user = userService.loginUser(request.getEmail(), request.getPassword());
        return buildAuthResponse(user);
    }

    public static class LoginRequest {
        @jakarta.validation.constraints.NotBlank(message = "Email is required")
        @jakarta.validation.constraints.Email(message = "Email must be valid")
        private String email;

        @jakarta.validation.constraints.NotBlank(message = "Password is required")
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
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
