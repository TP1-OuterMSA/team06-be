package com.example.teamproject.domain.ticket.dto;

import lombok.Data;

@Data
public class PurchaseResponseDto {
    private String message;
    private TicketDTO ticket;
}