CREATE SCHEMA auth;

CREATE TABLE auth.login_credentials (
                                        user_id UUID PRIMARY KEY REFERENCES core.users(id) ON DELETE CASCADE,
                                        password_hash TEXT NOT NULL,
                                        last_password_change TIMESTAMPTZ NOT NULL DEFAULT now()
);