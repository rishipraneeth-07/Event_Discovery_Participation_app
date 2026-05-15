package com.college.eventapp.service;

public interface PasswordResetService {
    String requestPasswordReset(String email);
    String resetPassword(String token, String newPassword);
}
