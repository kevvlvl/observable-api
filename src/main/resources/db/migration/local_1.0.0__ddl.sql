CREATE TABLE car(
    id SERIAL,
    name VARCHAR(20) NOT NULL,
    msrp NUMERIC NOT NULL,
    inventory NUMERIC,
    CONSTRAINT PK_car PRIMARY KEY (id)
);

INSERT INTO car(name, msrp, inventory)
VALUES('Audi A4', 44000, 1000);
INSERT INTO car(name, msrp, inventory)
VALUES('Audi S3', 48000, 2000);
INSERT INTO car(name, msrp, inventory)
VALUES('Audi S4', 61000, 1800);
INSERT INTO car(name, msrp, inventory)
VALUES('Audi RS6', 125000, 20);