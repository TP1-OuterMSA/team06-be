package com.example.teamproject.domain.ticket.dto;

import com.example.teamproject.domain.ticket.entity.Ticket;
import lombok.Data;

@Data
public class TicketDTO {
    private Long id;
    private String menu;
    private int price;
    private int count;
    private int totalCount;
    private boolean soldOut;

    public static TicketDTO fromEntity(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setMenu(ticket.getMenu());
        dto.setPrice(ticket.getPrice());
        dto.setCount(ticket.getCount());
        dto.setTotalCount(ticket.getTotalCount());
        dto.setSoldOut(ticket.isSoldOut());
        return dto;
    }
}
