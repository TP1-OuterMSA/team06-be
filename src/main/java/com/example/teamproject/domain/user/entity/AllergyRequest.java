package com.example.teamproject.domain.user.entity;

import com.example.teamproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class AllergyRequest {
    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private String allergyName;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime requestedAt;
    private LocalDateTime handledAt;
}
