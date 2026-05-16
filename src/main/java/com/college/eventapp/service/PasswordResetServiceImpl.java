package com.college.eventapp.service;

import com.college.eventapp.exception.BadRequestException;
import com.college.eventapp.model.PasswordResetToken;
import com.college.eventapp.model.User;
import com.college.eventapp.repository.PasswordResetTokenRepository;
import com.college.eventapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);
    private static final String GENERIC_FORGOT_PASSWORD_MESSAGE =
            "If an account with that email exists, a password reset link has been sent.";

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long tokenExpiryMinutes;
    private final long requestCooldownSeconds;

    public PasswordResetServiceImpl(UserRepository userRepository,
                                    PasswordResetTokenRepository passwordResetTokenRepository,
                                    BCryptPasswordEncoder passwordEncoder,
                                    MailService mailService,
                                    @Value("${app.password-reset.token-expiry-minutes:15}") long tokenExpiryMinutes,
                                    @Value("${app.password-reset.request-cooldown-seconds:60}") long requestCooldownSeconds) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.tokenExpiryMinutes = tokenExpiryMinutes;
        this.requestCooldownSeconds = requestCooldownSeconds;
    }

    @Override
    @Transactional
    public String requestPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email.trim());
        if (userOptional.isEmpty()) {
            return GENERIC_FORGOT_PASSWORD_MESSAGE;
        }

        User user = userOptional.get();
        LocalDateTime now = LocalDateTime.now();

        Optional<PasswordResetToken> latestToken = passwordResetTokenRepository.findTopByUserOrderByCreatedAtDesc(user);
        if (latestToken.isPresent()
                && !latestToken.get().isUsed()
                && latestToken.get().getCreatedAt().isAfter(now.minusSeconds(requestCooldownSeconds))
                && latestToken.get().getExpiresAt().isAfter(now)) {
            mailService.sendPasswordResetEmail(user.getEmail(), user.getName(), latestToken.get().getToken());
            return GENERIC_FORGOT_PASSWORD_MESSAGE;
        }

        invalidateUnusedTokens(user);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(generateToken());
        passwordResetToken.setCreatedAt(now);
        passwordResetToken.setExpiresAt(now.plusMinutes(tokenExpiryMinutes));
        passwordResetToken.setUsed(false);
        PasswordResetToken savedToken = passwordResetTokenRepository.save(passwordResetToken);

        mailService.sendPasswordResetEmail(user.getEmail(), user.getName(), savedToken.getToken());
        return GENERIC_FORGOT_PASSWORD_MESSAGE;
    }

    @Override
    @Transactional
    public String resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token.trim())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (passwordResetToken.isUsed() || passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invalid or expired reset token");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

        invalidateUnusedTokens(user);
        return "Password reset successfully.";
    }

    private void invalidateUnusedTokens(User user) {
        List<PasswordResetToken> activeTokens = passwordResetTokenRepository.findByUserAndUsedFalse(user);
        for (PasswordResetToken activeToken : activeTokens) {
            activeToken.setUsed(true);
        }
        passwordResetTokenRepository.saveAll(activeTokens);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
