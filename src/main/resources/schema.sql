CREATE TABLE residents (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           email VARCHAR(255) NOT NULL
);

CREATE TABLE apartments (
                            id SERIAL PRIMARY KEY,
                            number VARCHAR(50) NOT NULL,
                            resident_id BIGINT REFERENCES residents(id)
);

CREATE TABLE meters (
                        id SERIAL PRIMARY KEY,
                        apartment_id BIGINT REFERENCES apartments(id)
);

CREATE TABLE measurements (
                              id SERIAL PRIMARY KEY,
                              meter_id BIGINT REFERENCES meters(id),
                              value FLOAT NOT NULL,
                              date DATE NOT NULL
);

CREATE TABLE requests (
                          id SERIAL PRIMARY KEY,
                          apartment_id BIGINT NOT NULL,
                          description VARCHAR(500) NOT NULL,
                          status VARCHAR(50) NOT NULL,
                          created_at TIMESTAMP NOT NULL
);

CREATE TABLE bills (
                       id SERIAL PRIMARY KEY,
                       apartment_id BIGINT NOT NULL,
                       amount FLOAT NOT NULL,
                       period VARCHAR(50) NOT NULL
);
