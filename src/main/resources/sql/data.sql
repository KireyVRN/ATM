INSERT INTO clients (name, surname) VALUES('Василий', 'Ахметович');
INSERT INTO clients (name, surname) VALUES('Мария', 'Ивановна');
INSERT INTO clients (name, surname) VALUES('Jhon', 'Blackberry');
INSERT INTO clients (name, surname) VALUES('Яша', 'Дикий');
INSERT INTO clients (name, surname) VALUES('Adolf', 'Hauptbahnhof');

INSERT INTO cards(balance, card_number, pin, client_id) VALUES(300000.34, '11111111', '$2y$12$mYDzTv7h7DygQDYRas7Nd.JvyFuFjIYzOKYDPQPWIho5yGFhzHgRq', 1);
INSERT INTO cards(balance, card_number, pin, client_id) VALUES(449907.00, '22222222', '$2a$12$vDR1jO3Oeu7tt75HI/E5dOLVIDeXdczGceJIXjV6bjimCy0U2mK06', 1);
INSERT INTO cards(balance, card_number, pin, client_id) VALUES(110000.56, '33333333', '$2y$12$R2iMn7yRFT/0ZgvOl1FZteeR1UombzN2Ow6IFVhH20fBlI/lZ5uGO', 2);
INSERT INTO cards(balance, card_number, pin, client_id) VALUES(666666.32, '44444444', '$2y$12$V.8siaWtgMr6E.c.Msc2K.ff2puYDXiqeqTpu1JPObGAm49EZYKym', 2);
INSERT INTO cards(balance, card_number, pin, client_id) VALUES(7300000.00, '55555555', '$2y$12$Fz7eVV426ERfrHuiBBA6eOQoFCgRgc8AhvBB8fMWFEZC5fITFQ2oC', 3);
INSERT INTO cards(balance, card_number, pin, client_id) VALUES(777300000.98, '66666666', '$2y$12$2R6OIZ.O9Jrib4howVUDxOceosc0yLxtrwwPmRtVAuR8MiFjUns8G', 3);
INSERT INTO cards(balance, card_number, pin, client_id) VALUES(300000.77, '77777777', '$2y$12$gjVEaV.zcd2viNe6aALg7OrBD.Vk3qwUxJJ4f0sIJT39mFsYC5k6u', 3);
INSERT INTO cards(balance, card_number, pin, client_id) VALUES(7000.54, '88888888', '$2y$12$B9f1YR2fanbIwe5UN23cyeOQLRJIHGNvIwgUElb/cANN5hN0z.xYG', 3);
INSERT INTO cards(balance, card_number, pin, client_id) VALUES(456.22, '99999999', '$2y$12$iOGE13n.AS1p1rDoU3XUa.OnH1NTkq3eCMz1Wy8VR3mYZAaxnY62.', 4);
INSERT INTO cards(balance, card_number, pin, client_id) VALUES(90000009680.00, '01010101', '$2a$12$PRF8Jei1OAi7NVLR.Rm//O8iwfenkOhsVJ6O8xgH70XWnJLcNQfZG', 5);
INSERT INTO cards(balance, card_number, pin, client_id) VALUES(88889.99, '02020202', '$2a$12$maZLWwWiSuS1jSz88vAd2OgQJ/8AEFJZ4ATWQZG3PfaltT5MV8yba', 5);

INSERT INTO roles (name) VALUES ('CARD');

INSERT INTO cards_roles(card_id, role_id) VALUES (1,1);
INSERT INTO cards_roles(card_id, role_id) VALUES (2,1);
INSERT INTO cards_roles(card_id, role_id) VALUES (3,1);
INSERT INTO cards_roles(card_id, role_id) VALUES (4,1);
INSERT INTO cards_roles(card_id, role_id) VALUES (5,1);
INSERT INTO cards_roles(card_id, role_id) VALUES (6,1);
INSERT INTO cards_roles(card_id, role_id) VALUES (7,1);
INSERT INTO cards_roles(card_id, role_id) VALUES (8,1);
INSERT INTO cards_roles(card_id, role_id) VALUES (9,1);
INSERT INTO cards_roles(card_id, role_id) VALUES (10,1);
INSERT INTO cards_roles(card_id, role_id) VALUES (11,1);





