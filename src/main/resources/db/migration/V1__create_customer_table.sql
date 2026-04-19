CREATE TABLE customer (
    customer_id INTEGER generated always as identity primary key,
    first_name VARCHAR(32) not null,
    last_name VARCHAR(32) not null,
    id_code VARCHAR(11) not null unique
);
