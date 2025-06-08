CREATE TABLE IF NOT EXISTS sports_field
(
  id           SERIAL PRIMARY KEY NOT NULL,
  name         VARCHAR(255)       NOT NULL,
  latitude     DOUBLE PRECISION   NOT NULL,
  longitude    DOUBLE PRECISION   NOT NULL,
  city         VARCHAR(255)       NOT NULL,
  street       VARCHAR(255)       NOT NULL,
  zip_code     VARCHAR(20)        NOT NULL,
  country_code VARCHAR(3)         NOT NULL,
  description  VARCHAR(1024)
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
