package com.college.eventapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_interests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_interests_user_interest", columnNames = {"user_id", "interest"})
        },
        indexes = {
                @Index(name = "idx_user_interests_user_id", columnList = "user_id"),
                @Index(name = "idx_user_interests_interest", columnList = "interest"),
                @Index(name = "idx_user_interests_created_at", columnList = "created_at")
        }
)
@Data
public class UserInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String interest;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
