CREATE TABLE mfa_otp
(
    id         UUID PRIMARY KEY,
    user_id    UUID         NOT NULL,
    otp_hash   VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    attempts   INT DEFAULT 0,

    CONSTRAINT fk_mfa_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);

ALTER TABLE users
    ADD COLUMN mfa_enabled BOOLEAN DEFAULT FALSE;

ALTER TABLE users
    ADD COLUMN mfa_type VARCHAR(20) DEFAULT 'TOTP'
        CHECK (mfa_type IN ('NONE', 'TOTP', 'EMAIL_OTP'));
