CREATE ALIAS IF NOT EXISTS SYS_GUID FOR "org.tinywind.server.IsolatedDatabaseConfig.newGuid";
CREATE TABLE IF NOT EXISTS "user" (
    "id" VARCHAR(32) DEFAULT sys_guid() PRIMARY KEY,
    "grade" VARCHAR(32) NOT NULL CHECK("grade" IN ('ADMIN', 'NORMAL')),
    "login_id" VARCHAR(1024) NOT NULL UNIQUE,
    "password" VARCHAR(1024) NOT NULL,
    "name" VARCHAR(256) NOT NULL,
    "phone" VARCHAR(256) NOT NULL,
    "phone2" VARCHAR(256) NOT NULL,
    "email" VARCHAR(256) NOT NULL,
    "blackout" INTEGER DEFAULT 0 NOT NULL CHECK ("blackout" IN (0, 1)),
    "comment" VARCHAR(1024) NOT NULL,
    "created_at" TIMESTAMP DEFAULT current_timestamp NOT NULL
);
CREATE TABLE IF NOT EXISTS "user_authorization_code" (
    "user" VARCHAR(32) NOT NULL REFERENCES "user" ("id"),
    "code" VARCHAR(1024) NOT NULL,
    "expiring_time" TIMESTAMP NOT NULL,
    "created_at" TIMESTAMP DEFAULT current_timestamp NOT NULL,
    PRIMARY KEY ("user", "code")
);
CREATE TABLE IF NOT EXISTS "file" (
    "id" VARCHAR(32) DEFAULT sys_guid() PRIMARY KEY,
    "original_name" VARCHAR(1024) NOT NULL,
    "path" VARCHAR(1024) NOT NULL,
    "size" BIGINT NOT NULL,
    "created_at" TIMESTAMP DEFAULT current_timestamp NOT NULL
);
