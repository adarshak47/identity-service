ALTER TABLE refresh_tokens
    ADD COLUMN family_id UUID NOT NULL DEFAULT gen_random_uuid();

ALTER TABLE refresh_tokens
    ADD COLUMN replaced_by UUID;

ALTER TABLE refresh_tokens
    ADD COLUMN revoked_at TIMESTAMP;

ALTER TABLE refresh_tokens
    ADD COLUMN reuse_detected BOOLEAN DEFAULT FALSE;
