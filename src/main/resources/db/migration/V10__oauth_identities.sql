CREATE TABLE oauth_identities
(
    id               UUID PRIMARY KEY,
    provider         VARCHAR(50)  NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    email            VARCHAR(255),
    user_id          UUID         NOT NULL,
    created_at       TIMESTAMP    NOT NULL,

    CONSTRAINT fk_oauth_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),

    CONSTRAINT uq_provider_identity UNIQUE (provider, provider_user_id)
);
