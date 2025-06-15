CREATE TABLE IF NOT EXISTS app_user
(
  id       SERIAL PRIMARY KEY  NOT NULL,
  name     VARCHAR(255)        NOT NULL,
  username VARCHAR(50) UNIQUE  NOT NULL,
  email    VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255)        NOT NULL,
  role     VARCHAR(20)         NOT NULL
);

CREATE TABLE IF NOT EXISTS sports_field
(
  id           SERIAL PRIMARY KEY               NOT NULL,
  name         VARCHAR(255)                     NOT NULL,
  latitude     DOUBLE PRECISION                 NOT NULL,
  longitude    DOUBLE PRECISION                 NOT NULL,
  city         VARCHAR(255)                     NOT NULL,
  street       VARCHAR(255)                     NOT NULL,
  zip_code     VARCHAR(20)                      NOT NULL,
  country_code VARCHAR(3)                       NOT NULL,
  description  VARCHAR(1024),
  manager_id   INTEGER REFERENCES app_user (id) NOT NULL
);

CREATE TABLE IF NOT EXISTS sport_type
(
  id   SERIAL PRIMARY KEY NOT NULL,
  name VARCHAR(255)       NOT NULL,
  CONSTRAINT unique_sport_type_name UNIQUE (name)
);
CREATE UNIQUE INDEX IF NOT EXISTS unique_sport_type_name ON sport_type (name);

INSERT INTO sport_type (name)
VALUES ('BASKETBALL'),
       ('BEACH_VOLLEYBALL'),
       ('SOCCER'),
       ('TENNIS')
ON CONFLICT (name) DO NOTHING;

CREATE TABLE IF NOT EXISTS sports_field_sport_type
(
  id              SERIAL PRIMARY KEY                   NOT NULL,
  sports_field_id INTEGER REFERENCES sports_field (id) NOT NULL,
  sport_type_id   INTEGER REFERENCES sport_type (id)   NOT NULL,
  CONSTRAINT unique_sports_field_sport_type UNIQUE (sports_field_id, sport_type_id)
);
CREATE INDEX IF NOT EXISTS idx_sports_field_sport_type_sports_field_id
  ON sports_field_sport_type (sports_field_id);

CREATE TABLE IF NOT EXISTS reservation
(
  id              SERIAL PRIMARY KEY                   NOT NULL,
  owner_id        INTEGER REFERENCES app_user (id)     NOT NULL,
  sports_field_id INTEGER REFERENCES sports_field (id) NOT NULL,
  start_time      TIMESTAMP WITH TIME ZONE             NOT NULL,
  end_time        TIMESTAMP WITH TIME ZONE             NOT NULL,
  user_note       VARCHAR(1024),
  field_manager_note      VARCHAR(1024),
  CONSTRAINT unique_reservation UNIQUE (sports_field_id, start_time, end_time)
);
CREATE INDEX IF NOT EXISTS idx_reservation_sports_field_id
  ON reservation (sports_field_id);
CREATE INDEX IF NOT EXISTS idx_reservation_start_time
  ON reservation (start_time);
CREATE INDEX IF NOT EXISTS idx_reservation_end_time
  ON reservation (end_time);
