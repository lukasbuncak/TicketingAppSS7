CREATE SCHEMA core;

CREATE TYPE user_status AS ENUM ('ACTIVE','DISABLED');

CREATE TABLE core.users (
                            id UUID PRIMARY KEY,
                            personal_email TEXT NOT NULL UNIQUE,
                            school_email   TEXT NOT NULL UNIQUE,
                            display_name   TEXT,
                            status         user_status NOT NULL DEFAULT 'ACTIVE',
                            disabled_at     TIMESTAMPTZ,
                            created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                            updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);