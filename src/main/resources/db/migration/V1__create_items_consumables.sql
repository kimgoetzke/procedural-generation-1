CREATE TABLE ITEMS_CONSUMABLES
(
    id                LONG         NOT NULL AUTO_INCREMENT,
    name              VARCHAR(255) NOT NULL,
    tier              INT          NOT NULL DEFAULT 1,
    base_price        INT          NOT NULL,
    seller_type       VARCHAR(50)  NOT NULL DEFAULT 'GENERAL',
    effect_health     INT          NOT NULL DEFAULT 0,
    effect_max_health INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
-- AS SELECT * FROM CSVREAD('src/main/resources/content/consumables.csv');