package com.adarsh.identity_service.auth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "roles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    public Role(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
