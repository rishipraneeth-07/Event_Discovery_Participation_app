package com.college.eventapp.controller;

import com.college.eventapp.dto.UpdateUserRequestDTO;
import com.college.eventapp.dto.UserResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import com.college.eventapp.model.User;
import com.college.eventapp.security.CurrentUserService;
import com.college.eventapp.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    public UserController(UserService userService,
                          CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public UserResponseDTO getCurrentUser() {
        return convertToDTO(userService.getUserById(currentUserService.getCurrentUser().id()));
    }

    @PutMapping("/me")
    public UserResponseDTO updateCurrentUser(@Valid @RequestBody UpdateUserRequestDTO request) {
        User updated = userService.updateUser(
                currentUserService.getCurrentUser().id(),
                request.getName(),
                request.getEmail(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );
        return convertToDTO(updated);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable @Positive(message = "User id must be positive") Long id) {
        currentUserService.requireRole(com.college.eventapp.model.Role.ADMIN);
        User user = userService.getUserById(id);
        return convertToDTO(user);
    }


    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable @Positive(message = "User id must be positive") Long id,
                                      @Valid @RequestBody UpdateUserRequestDTO request) {
        currentUserService.requireRole(com.college.eventapp.model.Role.ADMIN);
        User updated = userService.updateUser(
                id,
                request.getName(),
                request.getEmail(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );
        return convertToDTO(updated);
    }

    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
