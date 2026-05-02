package com.adarsh.identity_service.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "permissions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Permission {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    public Permission(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
