package com.college.eventapp.repository;

import com.college.eventapp.model.Event;
import com.college.eventapp.model.EventStatus;
import com.college.eventapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByOrganizer(User organizer);
    List<Event> findByStatus(EventStatus status);
    List<Event> findByTitleContainingIgnoreCase(String title);
    Page<Event> findAll(Pageable pageable);
    Page<Event> findByStatus(EventStatus status, Pageable pageable);
    List<Event> findByStatusAndEventDateTimeAfter(EventStatus status, LocalDateTime eventDateTime);
    @Query("""
            SELECT e FROM Event e
            WHERE e.status = :status
            AND (
                LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """)
    Page<Event> searchByKeywordAndStatus(@Param("keyword") String keyword,
                                         @Param("status") EventStatus status,
                                         Pageable pageable);
    Page<Event> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String title, String description, String location, Pageable pageable);
}
