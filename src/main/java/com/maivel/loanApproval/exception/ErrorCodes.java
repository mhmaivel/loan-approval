package com.maivel.loanApproval.exception;

public final class ErrorCodes {

    private ErrorCodes() {}

    // 404
    public static final String LOAN_NOT_FOUND = "LOAN_NOT_FOUND";
    public static final String SCHEDULE_NOT_FOUND = "SCHEDULE_NOT_FOUND";
    public static final String PARAMETER_NOT_FOUND = "PARAMETER_NOT_FOUND";

    // 409
    public static final String ACTIVE_LOAN_EXISTS = "ACTIVE_LOAN_EXISTS";
    public static final String SCHEDULE_ALREADY_EXISTS = "SCHEDULE_ALREADY_EXISTS";
    public static final String CUSTOMER_NAME_EXISTS = "CUSTOMER_NAME_EXISTS";

    // 422
    public static final String INVALID_LOAN_STATUS = "INVALID_LOAN_STATUS";
    public static final String INVALID_ID_CODE = "INVALID_ID_CODE";
    public static final String INVALID_STATE = "INVALID_STATE";
    public static final String REJECTION_CANT_BE_NULL = "REJECTION_CANT_BE_NULL";

    // 400
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INVALID_PARAMETER_FORMAT = "INVALID_PARAMETER_FORMAT";

    // 500
    public static final String DATABASE_ERROR = "DATABASE_ERROR";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

}
