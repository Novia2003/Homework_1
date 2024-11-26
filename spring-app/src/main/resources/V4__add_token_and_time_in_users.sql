-- liquibase formatted sql

-- changeset vyach:1730917854686-1
ALTER TABLE users ADD time_sending_verification_code TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE users ADD token VARCHAR(255);

