CREATE SCHEMA IF NOT EXISTS core;
CREATE SCHEMA IF NOT EXISTS auth;

CREATE TYPE core.user_status AS ENUM ('ACTIVE','DISABLED');

CREATE TABLE core.users (
                            id INTEGER PRIMARY KEY,
                            personal_email TEXT NOT NULL UNIQUE,
                            school_email   TEXT NOT NULL UNIQUE,
                            display_name   TEXT,
                            status core.user_status NOT NULL DEFAULT 'ACTIVE',
                            disabled_at     TIMESTAMPTZ,
                            created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                            updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE auth.login_credentials (
                                        user_id INTEGER PRIMARY KEY REFERENCES core.users(id) ON DELETE CASCADE,
                                        password_hash TEXT NOT NULL,
                                        last_password_change TIMESTAMPTZ NOT NULL DEFAULT now()
);