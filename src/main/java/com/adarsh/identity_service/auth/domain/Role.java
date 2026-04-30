package com.adarsh.identity_service.auth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public Role(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }
}
