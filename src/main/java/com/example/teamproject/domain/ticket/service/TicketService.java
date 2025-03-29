package com.example.teamproject.domain.ticket.service;

import com.example.teamproject.domain.ticket.dto.PurchaseResponseDto;
import com.example.teamproject.domain.ticket.dto.TicketDTO;
import com.example.teamproject.domain.ticket.entity.Ticket;
import com.example.teamproject.domain.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository){
        this.ticketRepository = ticketRepository;
    }


    public PurchaseResponseDto purchaseTicket(Long ticketId) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        PurchaseResponseDto response = new PurchaseResponseDto();

        if (ticketOptional.isEmpty()) {
            response.setMessage("Ticket not found");
            response.setTicket(null);
            return response;
        }

        Ticket ticket = ticketOptional.get();

        if (ticket.isSoldOut()) {
            response.setMessage("Sold out");
            response.setTicket(TicketDTO.fromEntity(ticket));
            return response;
        }

        ticket.decreaseCount();
        ticketRepository.save(ticket);

        if (ticket.isSoldOut()) {
            response.setMessage("Sold out");
        } else {
            response.setMessage("Purchase successful");
        }
        response.setTicket(TicketDTO.fromEntity(ticket));
        return response;
    }
    public TicketDTO createTicket(TicketDTO ticketDto) {
        Ticket ticket = new Ticket();
        ticket.setMenu(ticketDto.getMenu());
        ticket.setPrice(ticketDto.getPrice());
        ticket.setCount(ticketDto.getCount());
        Ticket savedTicket = ticketRepository.save(ticket);
        return TicketDTO.fromEntity(savedTicket);
    }

    }

