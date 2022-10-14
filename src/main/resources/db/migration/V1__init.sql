-- Init script

-- DDL
CREATE TABLE airports(
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          airport_code VARCHAR(5) NOT NULL UNIQUE,
                          longtitude DOUBLE NOT NULL,
                          latitude DOUBLE NOT NULL
);
CREATE TABLE flights(
                             departure_id INT,
                             destination_id INT,
                             flight_number VARCHAR(30),
                             flight_date DATE not null,
                             CONSTRAINT FK_DESTINATION_ID FOREIGN KEY (destination_id) REFERENCES airports(id),
                             CONSTRAINT FK_DEPARTURE_ID FOREIGN KEY (departure_id) REFERENCES airports(id)
);
CREATE INDEX departure_id ON flights(departure_id);
CREATE INDEX destination_id ON flights(destination_id);
CREATE INDEX flight_number ON flights(flight_number);
CREATE INDEX flight_date ON flights(flight_date);
CREATE INDEX airport_code ON airports(airport_code);