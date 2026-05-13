package com.college.eventapp.service;

import com.college.eventapp.exception.BadRequestException;
import com.college.eventapp.exception.ConflictException;
import com.college.eventapp.exception.ResourceNotFoundException;
import com.college.eventapp.exception.UnauthorizedException;
import com.college.eventapp.model.Role;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.UserRepository;
import com.college.eventapp.security.CurrentUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           CurrentUserService currentUserService,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(User user) {
        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("Admin accounts cannot be self-registered");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email already registered");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return user;
    }

    @Override
    public User getUserById(Long id) {
        currentUserService.requireSameUserOrAdmin(id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, String name, String email,
                           String currentPassword, String newPassword) {
        currentUserService.requireSameUserOrAdmin(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (name != null && !name.isBlank()) {
            user.setName(name);
        }

        if (email != null && !email.isBlank() && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new BadRequestException("Email already in use");
            }
            user.setEmail(email);
        }

        if (newPassword != null && !newPassword.isBlank()) {
            if (currentPassword == null || currentPassword.isBlank()) {
                throw new BadRequestException("Current password is required");
            }
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new BadRequestException("Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }
}
