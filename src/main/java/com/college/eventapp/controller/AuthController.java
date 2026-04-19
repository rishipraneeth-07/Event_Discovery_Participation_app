package com.college.eventapp.controller;

import com.college.eventapp.dto.AuthRequestDTO;
import com.college.eventapp.dto.AuthResponseDTO;
import com.college.eventapp.dto.ForgotPasswordRequestDTO;
import com.college.eventapp.dto.MessageResponseDTO;
import com.college.eventapp.dto.ResetPasswordRequestDTO;
import jakarta.validation.Valid;
import com.college.eventapp.model.User;
import com.college.eventapp.service.PasswordResetService;
import com.college.eventapp.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    public AuthController(UserService userService,
                          PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
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

    @PostMapping("/forgot-password")
    public MessageResponseDTO forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        return new MessageResponseDTO(passwordResetService.requestPasswordReset(request.getEmail()));
    }

    @PostMapping("/reset-password")
    public MessageResponseDTO resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        return new MessageResponseDTO(passwordResetService.resetPassword(request.getToken(), request.getNewPassword()));
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
