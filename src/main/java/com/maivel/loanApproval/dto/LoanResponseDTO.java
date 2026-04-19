package com.maivel.loanApproval.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.maivel.loanApproval.enums.LoanStatus;
import com.maivel.loanApproval.enums.RejectionReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanResponseDTO {
    private Integer loanApplicationId;

    private String firstName;
    private String lastName;
    private String idCode;

    private Integer loanLengthMonths;
    private BigDecimal interestMargin;
    private BigDecimal baseInterestRate;
    private BigDecimal loanAmount;

    private LoanStatus status;
    private RejectionReason rejectionReason;
}
