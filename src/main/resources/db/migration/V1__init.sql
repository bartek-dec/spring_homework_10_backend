DROP TABLE IF EXISTS cars;

CREATE TABLE cars
(
    car_id          INT AUTO_INCREMENT PRIMARY KEY,
    brand           varchar(255) NOT NULL,
    model           varchar(255) NOT NULL,
    color           varchar(255) NOT NULL,
    production_year INT
)