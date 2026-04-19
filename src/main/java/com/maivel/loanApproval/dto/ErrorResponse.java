package com.maivel.loanApproval.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String errorCode;
    private final String message;
    private final String path;
    private final List<FieldViolation> fieldViolations;

    private ErrorResponse(Builder builder) {
        this.timestamp = LocalDateTime.now();
        this.status = builder.status;
        this.errorCode = builder.errorCode;
        this.message = builder.message;
        this.path = builder.path;
        this.fieldViolations = builder.fieldViolations;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int status;
        private String errorCode;
        private String message;
        private String path;
        private List<FieldViolation> fieldViolations;

        public Builder status(int status) { this.status = status; return this; }
        public Builder errorCode(String errorCode) { this.errorCode = errorCode; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder path(String path) { this.path = path; return this; }
        public Builder fieldViolations(List<FieldViolation> fieldViolations) { this.fieldViolations = fieldViolations; return this; }

        public ErrorResponse build() { return new ErrorResponse(this); }
    }

    public record FieldViolation(String field, String message) {}
}
