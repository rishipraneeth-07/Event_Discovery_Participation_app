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
        name = "saved_events",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_saved_events_user_event", columnNames = {"user_id", "event_id"})
        },
        indexes = {
                @Index(name = "idx_saved_events_user_id", columnList = "user_id"),
                @Index(name = "idx_saved_events_event_id", columnList = "event_id"),
                @Index(name = "idx_saved_events_created_at", columnList = "created_at")
        }
)
@Data
public class SavedEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
