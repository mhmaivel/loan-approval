CREATE TABLE loan_application (
    loan_application_id INTEGER generated always as identity primary key,
    customer_id INTEGER not null,
    loan_length_months INTEGER not null,
    loan_amount DECIMAL(15,2) not null,
    interest_margin DECIMAL(5,3) not null,
    base_interest_rate DECIMAL(5,3) not null,
    rejection_reason VARCHAR(255),
    status VARCHAR(50) not null,

    CONSTRAINT fk_loan_application_customer
        FOREIGN KEY (customer_id)
            REFERENCES customer (customer_id),
    CONSTRAINT chk_loan_application_status
        CHECK (status IN ('SUBMITTED', 'IN_REVIEW', 'APPROVED', 'REJECTED'))
);
