package com.example.teamproject.domain.ticket.repository;

import com.example.teamproject.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
