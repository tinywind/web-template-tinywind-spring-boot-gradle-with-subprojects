CREATE TABLE "user" (
    "id" RAW(32) DEFAULT sys_guid() PRIMARY KEY,
    "grade" CHAR(32) NOT NULL CHECK("grade" IN ('ADMIN', 'NORMAL')),
    "login_id" VARCHAR2(1024) NOT NULL UNIQUE,
    "password" VARCHAR2(1024) NOT NULL,
    "name" VARCHAR(256) NOT NULL,
    "phone" VARCHAR(256) NOT NULL,
    "phone2" VARCHAR(256) NOT NULL,
    "email" VARCHAR(256) NOT NULL,
    "blackout" FLOAT(1) DEFAULT 0 NOT NULL CHECK ("blackout" IN (0, 1)),
    "comment" VARCHAR(1024) NOT NULL,
    "created_at" TIMESTAMP(6) DEFAULT current_timestamp NOT NULL
);
COMMENT ON COLUMN "user"."blackout" IS '사용가능 여부';

CREATE TABLE "user_authorization_code" (
    "user" RAW(32) NOT NULL REFERENCES "user" ("id"),
    "code" VARCHAR2(1024) NOT NULL,
    "expiring_time" TIMESTAMP(6) NOT NULL,
    "created_at" TIMESTAMP(6) DEFAULT current_timestamp NOT NULL,

    PRIMARY KEY ("user", "code")
);
COMMENT ON TABLE "user_authorization_code" IS '사용자 인증 코드';

CREATE TABLE "file" (
    "id" RAW(32) DEFAULT sys_guid() PRIMARY KEY,
    "original_name" VARCHAR(1024) NOT NULL,
    "path" VARCHAR(1024) NOT NULL,
    "size" NUMBER(19) NOT NULL,
    "created_at" TIMESTAMP(6) DEFAULT current_timestamp NOT NULL
);
