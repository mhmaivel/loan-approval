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
@Table(name = "payment_schedule_item")
public class PaymentScheduleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer paymentScheduleItemId;

    @ManyToOne
    @JoinColumn(name = "payment_schedule_id")
    @Setter(AccessLevel.NONE)
    private PaymentSchedule paymentSchedule;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "payment_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "interest_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal interestAmount;

    @Column(name = "principal_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "remaining_principal", nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingPrincipal;


    public static PaymentScheduleItem create(PaymentSchedule paymentSchedule, Integer installmentNumber,
                               LocalDateTime paymentDate, BigDecimal paymentAmount,
                               BigDecimal interestAmount, BigDecimal principalAmount,
                               BigDecimal remainingPrincipal) {
        PaymentScheduleItem scheduleItem = new PaymentScheduleItem();
        scheduleItem.paymentSchedule = paymentSchedule;
        scheduleItem.installmentNumber = installmentNumber;
        scheduleItem.paymentDate = paymentDate;
        scheduleItem.paymentAmount = paymentAmount;
        scheduleItem.interestAmount = interestAmount;
        scheduleItem.principalAmount = principalAmount;
        scheduleItem.remainingPrincipal = remainingPrincipal;
        return scheduleItem;
    }

}
