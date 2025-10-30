CREATE TABLE instructor_requests (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(100) NOT NULL,
                                     cpf VARCHAR(11) NOT NULL,
                                     email VARCHAR(255) NOT NULL UNIQUE,
                                     motivation VARCHAR(2000),
                                     token VARCHAR(255) NOT NULL UNIQUE,
                                     expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                     status VARCHAR(20) NOT NULL,
                                     approved_at TIMESTAMP WITH TIME ZONE NULL
);
