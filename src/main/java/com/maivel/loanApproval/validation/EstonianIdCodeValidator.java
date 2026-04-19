package com.maivel.loanApproval.validation;

import com.maivel.loanApproval.annotation.EstonianIdCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.LocalDate;

public class EstonianIdCodeValidator implements ConstraintValidator<EstonianIdCode, String> {
    @Override
    public boolean isValid(String idCode, ConstraintValidatorContext context) {
        if (idCode == null || !idCode.matches("\\d{11}")) {
            return false;
        }
        int genderCentury = Character.getNumericValue(idCode.charAt(0));
        int year = Integer.parseInt(idCode.substring(1, 3));
        int month = Integer.parseInt(idCode.substring(3, 5));
        int day = Integer.parseInt(idCode.substring(5, 7));

        int century;
        switch (genderCentury) {
            case 1, 2 -> century = 1800;
            case 3, 4 -> century = 1900;
            case 5, 6 -> century = 2000;
            default -> { return false; }
        }
        try {
            LocalDate.of(century + year, month, day);
        } catch (DateTimeException e) {
            return false;
        }

        int last = Character.getNumericValue(idCode.charAt(10));
        int[] weight1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 1};
        int[] weight2 = {3, 4, 5, 6, 7, 8, 9, 1, 2, 3};

        int remainder1 = calculateCheck(idCode, weight1);
        if (remainder1 < 10 && remainder1 == last) return true;

        int remainder2 = calculateCheck(idCode, weight2);
        return (remainder2 < 10 && remainder2 == last) || (remainder2 == 10 && last == 0);
    }

    private int calculateCheck(String code, int[] weights) {
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (code.charAt(i) - '0') * weights[i];
        }
        return sum % 11;
    }
}
