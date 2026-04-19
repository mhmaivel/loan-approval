package com.maivel.loanApproval.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "payment_schedule")
public class PaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer paymentScheduleId;

    @OneToOne
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    @Column(name = "first_payment_date", nullable = false)
    private LocalDateTime firstPaymentDate;

    @Column(name = "last_payment_date", nullable = false)
    private LocalDateTime lastPaymentDate;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "total_interest", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalInterest;


    public static PaymentSchedule create(LoanApplication loanApplication,
                                         LocalDateTime firstPaymentDate,
                                         LocalDateTime lastPaymentDate,
                                         BigDecimal totalAmount,
                                         BigDecimal totalInterest) {
        PaymentSchedule schedule = new PaymentSchedule();
        schedule.loanApplication = loanApplication;
        schedule.firstPaymentDate = firstPaymentDate;
        schedule.lastPaymentDate = lastPaymentDate;
        schedule.totalAmount = totalAmount;
        schedule.totalInterest = totalInterest;
        return schedule;
    }

}
