CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY(user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY(user_id)
            REFERENCES users(id),

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY(role_id)
            REFERENCES roles(id)
);

-- Seed roles
INSERT INTO roles (id, name) VALUES
    (gen_random_uuid(), 'USER'),
    (gen_random_uuid(), 'ADMIN');
