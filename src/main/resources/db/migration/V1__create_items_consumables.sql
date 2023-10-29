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

INSERT INTO ITEMS_CONSUMABLES (name, tier, base_price, seller_type, effect_health)
VALUES ('Weak Health Potion', 1, 85, 'GENERAL', 30);
INSERT INTO ITEMS_CONSUMABLES (name, tier, base_price, seller_type, effect_health)
VALUES ('Small Health Potion', 1, 100, 'GENERAL', 60);
INSERT INTO ITEMS_CONSUMABLES (name, tier, base_price, seller_type, effect_health)
VALUES ('Small Health Potion', 1, 100, 'ALCHEMY', 60);
INSERT INTO ITEMS_CONSUMABLES (name, tier, base_price, seller_type, effect_health)
VALUES ('Regular Health Potion', 1, 150, 'ALCHEMY', 90);
INSERT INTO ITEMS_CONSUMABLES (name, tier, base_price, seller_type, effect_health, effect_max_health)
VALUES ('Special House Potion', 1, 250, 'ALCHEMY', 150, 10);
INSERT INTO ITEMS_CONSUMABLES (name, tier, base_price, seller_type, effect_max_health)
VALUES ('Potion of Hope', 1, 1000, 'GENERAL', 50);