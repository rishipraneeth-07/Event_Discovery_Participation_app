package com.college.eventapp.repository;

import com.college.eventapp.model.Event;
import com.college.eventapp.model.RecentlyViewedEvent;
import com.college.eventapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentlyViewedEventRepository extends JpaRepository<RecentlyViewedEvent, Long> {
    Optional<RecentlyViewedEvent> findByUserAndEvent(User user, Event event);
    List<RecentlyViewedEvent> findByUserOrderByViewedAtDesc(User user);
    long countByEvent(Event event);
    void deleteByEvent(Event event);
}
