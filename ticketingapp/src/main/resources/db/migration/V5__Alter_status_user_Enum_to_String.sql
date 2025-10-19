-- 1) Drop the default temporarily (PG requires this to change types cleanly)
ALTER TABLE core.users
    ALTER COLUMN status DROP DEFAULT;

-- 2) Convert the column to TEXT and cast existing values from enum -> text
ALTER TABLE core.users
    ALTER COLUMN status TYPE TEXT USING status::text;

-- 3) Restore the default
ALTER TABLE core.users
    ALTER COLUMN status SET DEFAULT 'ACTIVE';

ALTER TABLE core.users
    ADD CONSTRAINT users_status_chk
        CHECK (status IN ('ACTIVE','DISABLED'));