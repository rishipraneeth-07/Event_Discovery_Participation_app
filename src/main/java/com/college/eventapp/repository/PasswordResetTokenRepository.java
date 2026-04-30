package com.college.eventapp.repository;

import com.college.eventapp.model.PasswordResetToken;
import com.college.eventapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    List<PasswordResetToken> findByUserAndUsedFalse(User user);
    Optional<PasswordResetToken> findTopByUserOrderByCreatedAtDesc(User user);
    long countByUserAndCreatedAtAfter(User user, LocalDateTime createdAt);
}
