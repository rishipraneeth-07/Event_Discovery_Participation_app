package com.college.eventapp.service;

import com.college.eventapp.model.Role;
import com.college.eventapp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> loginUser(String email, String password);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    List<User> getUsersByRole(Role role);
}
