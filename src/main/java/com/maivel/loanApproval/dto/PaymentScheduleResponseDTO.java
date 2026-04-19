package com.maivel.loanApproval.dto;

import com.maivel.loanApproval.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentScheduleResponseDTO {
    private String fullName;
    private String idCode;

    private BigDecimal loanAmount;
    private BigDecimal interestMargin;
    private BigDecimal baseInterestRate;
    private Integer loanLengthMonths;

    private Integer paymentScheduleId;
    private LocalDateTime firstPaymentDate;
    private LocalDateTime lastPaymentDate;
    private BigDecimal totalAmount;
    private BigDecimal totalInterest;

    private LoanStatus loanStatus;

    private List<PaymentScheduleItemDTO> items;
}
