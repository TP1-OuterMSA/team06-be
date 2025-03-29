package com.example.teamproject.domain.ticket.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menu;
    private int price;
    private int count;

    public boolean isSoldOut(){
        return count <= 0;
    }

    public void decreaseCount(){
        if(count>0){
            count--;
        }
    }

}
