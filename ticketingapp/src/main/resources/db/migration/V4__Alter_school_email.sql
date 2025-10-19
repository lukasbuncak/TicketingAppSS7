ALTER TABLE core.users DROP COLUMN school_email;
ALTER TABLE core.users
    ADD COLUMN school_email TEXT
        GENERATED ALWAYS AS ('s' || id || '@fontys.nl') STORED;
ALTER TABLE core.users
    ADD CONSTRAINT uq_users_school_email UNIQUE (school_email);