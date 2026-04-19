CREATE TABLE payment_schedule (
    payment_schedule_id INTEGER generated always as identity primary key,
    loan_application_id INTEGER not null,
    first_payment_date TIMESTAMP not null,
    last_payment_date TIMESTAMP not null,
    total_amount DECIMAL(15,2) not null,
    total_interest DECIMAL(15,2) not null,


    CONSTRAINT fk_payment_schedule_loan_application
        FOREIGN KEY (loan_application_id)
            REFERENCES loan_application(loan_application_id),
    CONSTRAINT uq_one_schedule_per_application UNIQUE (loan_application_id)
);
CREATE UNIQUE INDEX uq_one_active_loan_per_customer
    ON loan_application (customer_id)
    WHERE status IN ('SUBMITTED', 'IN_REVIEW');