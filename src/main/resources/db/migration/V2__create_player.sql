CREATE TABLE PLAYER
(
    id             VARCHAR(50)  NOT NULL,
    name           VARCHAR(200) NOT NULL,
    location_name  VARCHAR(200) NOT NULL,
    poi_name       VARCHAR(200) NOT NULL,
    x              INT          NOT NULL,
    y              INT          NOT NULL,
    gold           INT          NOT NULL,
    min_damage     INT          NOT NULL,
    max_damage     INT          NOT NULL,
    health         INT          NOT NULL,
    max_health     INT          NOT NULL,
    experience     INT          NOT NULL,
    level          INT          NOT NULL,
    previous_state VARCHAR(50)  NOT NULL,
    current_state  VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id)
);