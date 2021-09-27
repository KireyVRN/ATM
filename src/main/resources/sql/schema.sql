CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT,
    name VARCHAR(255),
    surname VARCHAR(255),
    PRIMARY KEY (ID)
);

CREATE TABLE cards (
    id BIGINT AUTO_INCREMENT,
    balance DECIMAL(19,2),
    card_number VARCHAR(255),
    pin VARCHAR(255),
    client_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE operations (
    id BIGINT AUTO_INCREMENT,
    amount_of_money DECIMAL(13,2) not null,
    date_and_time TIMESTAMP,
    from_card VARCHAR(255),
    to_card VARCHAR(255),
    operation_type VARCHAR(255),

    card_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE cards_roles (
    card_id BIGINT not null,
    role_id BIGINT not null,
    PRIMARY KEY (card_id, role_id)
);
