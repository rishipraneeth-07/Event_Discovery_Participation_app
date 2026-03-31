package com.college.eventapp.controller;

import com.college.eventapp.dto.UpdateUserRequestDTO;
import com.college.eventapp.dto.UserResponseDTO;
import com.college.eventapp.model.User;
import com.college.eventapp.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return convertToDTO(user);
    }


    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id,
                                      @RequestBody UpdateUserRequestDTO request) {
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