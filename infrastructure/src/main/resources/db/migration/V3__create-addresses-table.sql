CREATE TABLE addresses (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    title VARCHAR(100) NOT NULL,
    zip_code VARCHAR(50) NOT NULL,
    number CHAR(30) NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    district VARCHAR(255) NOT NULL,
    country CHAR(60) NOT NULL,
    state CHAR(60) NOT NULL,
    complement VARCHAR(255),
    is_default BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL
);