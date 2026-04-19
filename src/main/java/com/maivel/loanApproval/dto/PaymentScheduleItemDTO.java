package com.maivel.loanApproval.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentScheduleItemDTO {
    private Integer installmentNumber;
    private LocalDateTime paymentDate;
    private BigDecimal paymentAmount;
    private BigDecimal interestAmount;
    private BigDecimal principalAmount;
    private BigDecimal remainingPrincipal;
}
