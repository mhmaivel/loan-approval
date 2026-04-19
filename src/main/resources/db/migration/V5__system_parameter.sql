CREATE TABLE system_parameter (
    parameter_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    parameter_key VARCHAR(100) NOT NULL UNIQUE,
    parameter_value VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO system_parameter (parameter_key, parameter_value)
VALUES
    ('MAX_CUSTOMER_AGE', '70'),
    ('BASE_INTEREST_RATE_6M', '3.856');