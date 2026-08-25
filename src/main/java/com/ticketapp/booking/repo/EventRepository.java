package com.ticketapp.booking.repo;

import com.ticketapp.booking.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
  boolean existsByTitle(String title);
    
}
