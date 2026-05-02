CREATE TABLE audit_logs
(
    id        UUID PRIMARY KEY,
    action    VARCHAR(100) NOT NULL,
    actor     VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP    NOT NULL,
    details   VARCHAR(1000),
    remote_ip VARCHAR(100) NOT NULL
);
