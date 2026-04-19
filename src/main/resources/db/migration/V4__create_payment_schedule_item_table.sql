CREATE TABLE payment_schedule_item (
    payment_schedule_item_id INTEGER generated always as identity primary key,
    payment_schedule_id INTEGER not null ,
    installment_number INTEGER not null,
    payment_date TIMESTAMP not null,
    payment_amount DECIMAL(15,2) not null,
    interest_amount DECIMAL(15,2) not null,
    principal_amount DECIMAL(15,2) not null,
    remaining_principal DECIMAL(15,2) not null,

    CONSTRAINT fk_payment_schedule_item_payment_schedule
        FOREIGN KEY (payment_schedule_id)
            REFERENCES payment_schedule(payment_schedule_id)
);
