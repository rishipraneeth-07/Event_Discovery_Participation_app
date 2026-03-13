package com.college.eventapp.repository;

import com.college.eventapp.model.Notification;
import com.college.eventapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
    List<Notification> findByUserAndRead(User user, boolean read);
    boolean existsByUserAndRead(User user, boolean read);
}
