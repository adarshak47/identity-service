ALTER TABLE refresh_tokens
    ADD COLUMN device_name VARCHAR(255),
    ADD COLUMN ip_address VARCHAR(100),
    ADD COLUMN user_agent TEXT;
