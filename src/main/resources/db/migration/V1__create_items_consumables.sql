CREATE TABLE ITEMS_CONSUMABLES
(
    id            LONG         NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    tier          INT          NOT NULL DEFAULT 1,
    effect_health INT          NOT NULL DEFAULT 0,
    base_price    INT          NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO ITEMS_CONSUMABLES (name, tier, effect_health, base_price)
VALUES ('Weak Health Potion', 1, 30, 15);
INSERT INTO ITEMS_CONSUMABLES (name, tier, effect_health, base_price)
VALUES ('Simple Health Potion', 1, 60, 25);
INSERT INTO ITEMS_CONSUMABLES (name, tier, effect_health, base_price)
VALUES ('Medium Health Potion', 1, 100, 40);
INSERT INTO ITEMS_CONSUMABLES (name, tier, effect_health, base_price)
VALUES ('Strong Health Potion', 1, 150, 60);