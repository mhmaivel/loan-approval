package com.maivel.loanApproval.exception;

import lombok.Getter;

@Getter
public class InvalidStateException extends RuntimeException {
    private final String errorCode;

    public InvalidStateException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
