CREATE TABLE customers (
    id UUID PRIMARY KEY,
    idp_user_id UUID NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(110) NOT NULL,
    last_name VARCHAR(110) NOT NULL,
    document_number VARCHAR(16) UNIQUE,
    document_type VARCHAR(5),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL
);