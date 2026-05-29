package com.college.eventapp.repository;

import com.college.eventapp.model.Event;
import com.college.eventapp.model.SavedEvent;
import com.college.eventapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedEventRepository extends JpaRepository<SavedEvent, Long> {
    boolean existsByUserAndEvent(User user, Event event);
    Optional<SavedEvent> findByUserAndEvent(User user, Event event);
    List<SavedEvent> findByUserOrderByCreatedAtDesc(User user);
    long countByEvent(Event event);
    void deleteByEvent(Event event);
}
