package com.college.eventapp.Repository;

import com.college.eventapp.model.Event;
import com.college.eventapp.model.EventStatus;
import com.college.eventapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByOrganizer(User organizer);
    List<Event> findByStatus(EventStatus status);
}
