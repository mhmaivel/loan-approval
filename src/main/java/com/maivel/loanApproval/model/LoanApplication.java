package com.maivel.loanApproval.model;

import com.maivel.loanApproval.exception.BusinessException;
import com.maivel.loanApproval.exception.ErrorCodes;
import com.maivel.loanApproval.enums.LoanStatus;
import com.maivel.loanApproval.enums.RejectionReason;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "loan_application")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer loanApplicationId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "loan_length_months", nullable = false)
    private Integer loanLengthMonths;

    @Column(name = "loan_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "interest_margin", nullable = false, precision = 5, scale = 3)
    private BigDecimal interestMargin;

    @Column(name = "base_interest_rate", nullable = false, precision = 5, scale = 3)
    private BigDecimal baseInterestRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "rejection_reason")
    private RejectionReason rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    public static LoanApplication create (Customer customer,
                                          Integer loanLengthMonths,
                                          BigDecimal loanAmount,
                                          BigDecimal interestMargin,
                                          BigDecimal baseInterestRate) {
        LoanApplication app = new LoanApplication();
        app.customer = customer;
        app.loanLengthMonths = loanLengthMonths;
        app.loanAmount = loanAmount;
        app.interestMargin = interestMargin;
        app.baseInterestRate = baseInterestRate;
        app.rejectionReason = null;
        app.status = LoanStatus.SUBMITTED;
        return app;
    }

    public void moveToInReview() {
        if(this.status != LoanStatus.SUBMITTED) {
            throw new BusinessException(ErrorCodes.INVALID_STATE, "Loan application can be moved to IN_REVIEW only from SUBMITTED");
        }
        this.status = LoanStatus.IN_REVIEW;
    }

    public void approve() {
        if(this.status != LoanStatus.IN_REVIEW) {
            throw new BusinessException(ErrorCodes.INVALID_STATE, "Loan application can be approved only from IN_REVIEW");
        }
        this.status = LoanStatus.APPROVED;
        this.rejectionReason = null;
    }

    public void reject(RejectionReason reason) {
        if (reason == null) {
            throw new BusinessException(ErrorCodes.REJECTION_CANT_BE_NULL, "Rejection reason can't be null");
        }
        if (this.status != LoanStatus.SUBMITTED && this.status != LoanStatus.IN_REVIEW) {
            throw new BusinessException(ErrorCodes.INVALID_STATE, "Loan application can be rejected from SUBMITTED or IN_REVIEW");
        }
        this.status = LoanStatus.REJECTED;
        this.rejectionReason = reason;
    }
}
