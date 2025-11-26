CREATE DATABASE derm;

USE derm; 

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

INSERT INTO users (username, password) VALUES ('hello', '123');
INSERT INTO users (username, password) VALUES ('admin', 'password');
INSERT INTO users (username, password) VALUES ('user1', 'mypassword');

SELECT * FROM users;
