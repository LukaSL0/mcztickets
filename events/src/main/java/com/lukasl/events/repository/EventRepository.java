package com.lukasl.events.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lukasl.events.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByEventDateAfter(LocalDateTime date);
    List<Event> findByAvailableTicketsGreaterThan(Integer tickets);

    @Modifying
    @Query("""
        UPDATE Event e
        SET e.availableTickets = e.availableTickets - :tickets
        WHERE e.id = :eventId
        AND e.availableTickets >= :tickets
    """)
    int reserveTickets(@Param("eventId") Long eventId, @Param("tickets") int tickets);
}
