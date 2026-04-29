package com.college.eventapp.repository;

import com.college.eventapp.model.Event;
import com.college.eventapp.model.EventModerationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventModerationLogRepository extends JpaRepository<EventModerationLog, Long> {
    List<EventModerationLog> findByEventOrderByCreatedAtDesc(Event event);
    void deleteByEvent(Event event);
}
