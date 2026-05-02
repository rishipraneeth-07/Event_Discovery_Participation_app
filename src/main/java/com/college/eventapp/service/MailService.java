package com.college.eventapp.service;

public interface MailService {
    void sendPasswordResetEmail(String recipientEmail, String recipientName, String token);
}
