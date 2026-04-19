package com.maivel.loanApproval.dto;

import com.maivel.loanApproval.annotation.EstonianIdCode;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class LoanRequestDTO {
    @NotBlank(message = "First name is required")
    @Size(max = 32, message = "First name can be up to 32 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 32, message = "Last name can be up to 32 characters")
    private String lastName;

    @NotBlank(message = "ID code is required")
    @Size(max = 11, min = 11, message = "ID code must be 11 digits long.")
    @EstonianIdCode
    private String idCode;

    @NotNull(message = "Loan length must be filled")
    @Min(value = 6, message = "Cannot be less than 6 months")
    @Max(value = 360, message = "Cannot be more than 360 months")
    private Integer loanLengthMonths;

    @NotNull(message = "Interest margin must be filled")
    @DecimalMin(value = "0", message = "Cannot be less than 0")
    private BigDecimal interestMargin;

    @NotNull(message = "Loan amount must be filled")
    @DecimalMin(value = "5000", message = "Cannot be less than 5000 euros")
    private BigDecimal loanAmount;
}
