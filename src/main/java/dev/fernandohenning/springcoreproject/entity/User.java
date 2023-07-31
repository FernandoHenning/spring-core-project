package dev.fernandohenning.springcoreproject.entity;

import dev.fernandohenning.springcoreproject.model.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(length = 25)
    private String firstName;

    @Column(length = 25)
    private String lastName;

    @Column(length = 100)
    private String email;

    private String password;

    @Transient
    private String passwordConfirmation;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Role role;
    /**
     * User is not enable by default. The account must be activated by e-mail first.
     */
    @Column(columnDefinition = "boolean default false")
    private boolean isEnabled;

    @Column(columnDefinition = "boolean default false")
    private boolean isLocked;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;
}

