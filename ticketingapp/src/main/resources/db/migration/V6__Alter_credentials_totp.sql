ALTER TABLE auth.login_credentials
    ADD COLUMN totp_enabled BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE auth.login_credentials
    ADD COLUMN totp_secret TEXT;

ALTER TABLE auth.login_credentials
    ADD COLUMN totp_pending BOOLEAN NOT NULL DEFAULT FALSE;
