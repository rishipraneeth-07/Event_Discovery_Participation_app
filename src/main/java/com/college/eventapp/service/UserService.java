package com.college.eventapp.service;

import com.college.eventapp.model.Role;
import com.college.eventapp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    User loginUser(String email, String password);
    User getUserById(Long id);
    User getUserByEmail(String email);
    List<User> getUsersByRole(Role role);
    List<User> getAllUsers();
}
