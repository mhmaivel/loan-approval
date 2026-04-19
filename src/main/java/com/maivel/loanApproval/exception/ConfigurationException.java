package com.maivel.loanApproval.exception;

import lombok.Getter;

@Getter
public class ConfigurationException extends RuntimeException {
    private final String errorCode;

    public ConfigurationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
