package com.spoorthy.ems.repository;

import com.spoorthy.ems.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatusOrderByEventDateAsc(Event.Status status);

    List<Event> findByClubIdOrderByEventDateAsc(Long clubId);

    List<Event> findByClubIdAndStatusOrderByEventDateAsc(Long clubId, Event.Status status);

    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.eventDate >= :today ORDER BY e.eventDate ASC, e.eventTime ASC")
    List<Event> findUpcomingEvents(@Param("status") Event.Status status, @Param("today") LocalDate today);

    @Query("SELECT COUNT(r) FROM EventRegistration r WHERE r.event.id = :eventId")
    Long countRegistrationsByEventId(@Param("eventId") Long eventId);
}
