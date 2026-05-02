CREATE TABLE email_verification_tokens
(
    id         UUID PRIMARY KEY,
    token      VARCHAR(255) UNIQUE NOT NULL,
    user_id    UUID                NOT NULL,
    expires_at TIMESTAMP           NOT NULL,
    verified   BOOLEAN             NOT NULL,
    CONSTRAINT fk_email_verification_user FOREIGN KEY (user_id) REFERENCES users (id)
);
