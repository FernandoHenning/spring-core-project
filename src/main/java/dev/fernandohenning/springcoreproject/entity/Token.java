package dev.fernandohenning.springcoreproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String token;

    private boolean isRevoked;

    private boolean isExpired;

    @ManyToOne
    @JoinColumn(name =  "user_id")
    private User user;
}
