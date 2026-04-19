package com.maivel.loanApproval.annotation;

import com.maivel.loanApproval.validation.EstonianIdCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = EstonianIdCodeValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EstonianIdCode {
    String message() default "Invalid ID code number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
