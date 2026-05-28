package com.college.eventapp.repository;

import com.college.eventapp.model.Event;
import com.college.eventapp.model.Registration;
import com.college.eventapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository  extends JpaRepository<Registration,Long> {
    List<Registration> findByUser(User user);
    List<Registration> findByUserOrderByEventEventDateTimeDesc(User user);
    List<Registration> findByEvent(Event event);
    void deleteByEvent(Event event);
    boolean existsByUserAndEvent(User user,Event event);
    java.util.Optional<Registration> findByUserAndEvent(User user, Event event);
    long countByEvent(Event event);
}
