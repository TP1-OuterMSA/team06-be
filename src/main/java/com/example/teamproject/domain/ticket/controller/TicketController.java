package com.example.teamproject.domain.ticket.controller;

import com.example.teamproject.domain.ticket.dto.PurchaseResponseDto;
import com.example.teamproject.domain.ticket.dto.TicketDTO;
import com.example.teamproject.domain.ticket.service.TicketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/team6/ticket")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }
    @GetMapping
    public TicketDTO getTicket(){
        return ticketService.getTicket();
    }
    @PostMapping("/{id}/purchase")
    public PurchaseResponseDto purchaseTicket(@PathVariable Long id) {
        return ticketService.purchaseTicket(id);
    }

    @PostMapping("/create")
    public TicketDTO createTicket(@RequestBody TicketDTO ticketDto) {
        return ticketService.createTicket(ticketDto);
    }

}
