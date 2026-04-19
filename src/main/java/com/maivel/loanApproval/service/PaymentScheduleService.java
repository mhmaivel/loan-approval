package com.maivel.loanApproval.service;

import com.maivel.loanApproval.dto.PaymentScheduleResponseDTO;
import com.maivel.loanApproval.enums.LoanStatus;
import com.maivel.loanApproval.enums.RejectionReason;
import com.maivel.loanApproval.exception.ConflictException;
import com.maivel.loanApproval.exception.ErrorCodes;
import com.maivel.loanApproval.exception.InvalidStateException;
import com.maivel.loanApproval.exception.NotFoundException;
import com.maivel.loanApproval.mapper.PaymentScheduleMapper;
import com.maivel.loanApproval.model.LoanApplication;
import com.maivel.loanApproval.model.PaymentSchedule;
import com.maivel.loanApproval.model.PaymentScheduleItem;
import com.maivel.loanApproval.repository.LoanApplicationRepository;
import com.maivel.loanApproval.repository.PaymentScheduleItemRepository;
import com.maivel.loanApproval.repository.PaymentScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentScheduleService {
    private final PaymentScheduleRepository scheduleRepo;
    private final PaymentScheduleItemRepository scheduleItemRepo;
    private final LoanApplicationRepository loanRepo;

    public PaymentScheduleService(PaymentScheduleRepository scheduleRepo,
                                  PaymentScheduleItemRepository scheduleItemRepo,
                                  LoanApplicationRepository loanRepo) {
        this.scheduleRepo = scheduleRepo;
        this.scheduleItemRepo = scheduleItemRepo;
        this.loanRepo = loanRepo;
    }

    @Transactional
    public PaymentScheduleResponseDTO generateSchedule(Integer loanApplicationId) {
        LoanApplication loan = loanRepo.findById(loanApplicationId)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.LOAN_NOT_FOUND, "Loan application not found"));

        validateLoanEligibleForScheduleGeneration(loan);

        boolean scheduleAlreadyExists = scheduleRepo.existsByLoanApplication(loan);
        if (scheduleAlreadyExists) {
            throw new ConflictException(ErrorCodes.SCHEDULE_ALREADY_EXISTS,
                    "A payment schedule already exists for this loan application");
        }

        BigDecimal principal = loan.getLoanAmount();
        int months = loan.getLoanLengthMonths();
        BigDecimal monthlyRate = resolveMonthlyRate(loan);
        BigDecimal monthlyPayment = calculateMonthlyPayment(principal, monthlyRate, months);
        LocalDateTime firstPaymentDate = LocalDateTime.now().toLocalDate().atStartOfDay();

        PaymentSchedule schedule = PaymentSchedule.create(
                loan,
                firstPaymentDate,
                firstPaymentDate.plusMonths(months - 1),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        scheduleRepo.save(schedule);

        List<PaymentScheduleItem> items = buildItems(schedule, principal, monthlyRate, months, monthlyPayment, firstPaymentDate);
        scheduleItemRepo.saveAll(items);

        updateScheduleTotals(schedule, items);
        scheduleRepo.save(schedule);

        loan.moveToInReview();
        loanRepo.save(loan);

        return PaymentScheduleMapper.toResponseDTO(schedule, items);
    }

    @Transactional
    public PaymentScheduleResponseDTO approveSchedule(Integer paymentScheduleId) {
        PaymentSchedule schedule = getSchedule(paymentScheduleId);
        LoanApplication loan = schedule.getLoanApplication();

        validateLoanInReview(loan, "approved");
        loan.approve();
        loanRepo.save(loan);

        return PaymentScheduleMapper.toResponseDTO(schedule, scheduleItemRepo.findByPaymentSchedule(schedule));
    }

    @Transactional
    public PaymentScheduleResponseDTO rejectSchedule(Integer paymentScheduleId, RejectionReason reason) {
        PaymentSchedule schedule = getSchedule(paymentScheduleId);
        LoanApplication loan = schedule.getLoanApplication();

        validateLoanInReview(loan, "rejected");
        loan.reject(reason);
        loanRepo.save(loan);

        return PaymentScheduleMapper.toResponseDTO(schedule, scheduleItemRepo.findByPaymentSchedule(schedule));
    }

    private void validateLoanEligibleForScheduleGeneration(LoanApplication loan) {
        LoanStatus status = loan.getStatus();

        if (status == LoanStatus.REJECTED) {
            throw new InvalidStateException(ErrorCodes.INVALID_LOAN_STATUS,
                    "Cannot generate a schedule for a rejected loan application");
        }
        if (status == LoanStatus.APPROVED) {
            throw new InvalidStateException(ErrorCodes.INVALID_LOAN_STATUS,
                    "Cannot generate a schedule for an already approved loan application");
        }
        if (status == LoanStatus.IN_REVIEW) {
            throw new InvalidStateException(ErrorCodes.INVALID_LOAN_STATUS,
                    "Cannot generate a schedule for a loan application that is already in review");
        }
    }

    private void validateLoanInReview(LoanApplication loan, String attemptedAction) {
        if (loan.getStatus() != LoanStatus.IN_REVIEW) {
            throw new InvalidStateException(ErrorCodes.INVALID_LOAN_STATUS,
                    "Loan application cannot be " + attemptedAction + " — must be IN_REVIEW (current: " + loan.getStatus() + ")");
        }
    }

    private BigDecimal resolveMonthlyRate(LoanApplication loan) {
        BigDecimal annualRate = loan.getInterestMargin().add(loan.getBaseInterestRate());
        return annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
    }

    private List<PaymentScheduleItem> buildItems(PaymentSchedule schedule, BigDecimal principal, BigDecimal monthlyRate,
                                                 int months, BigDecimal monthlyPayment, LocalDateTime firstPaymentDate) {
        List<PaymentScheduleItem> items = new ArrayList<>();
        BigDecimal remainingPrincipal = principal;

        for (int i = 1; i <= months; i++) {
            BigDecimal interestAmount = remainingPrincipal.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalAmount = monthlyPayment.subtract(interestAmount).setScale(2, RoundingMode.HALF_UP);
            BigDecimal paymentAmountForMonth = monthlyPayment;

            if (i == months) {
                principalAmount = remainingPrincipal;
                paymentAmountForMonth = remainingPrincipal.add(interestAmount).setScale(2, RoundingMode.HALF_UP);
                remainingPrincipal = BigDecimal.ZERO;
            } else {
                remainingPrincipal = remainingPrincipal.subtract(principalAmount).setScale(2, RoundingMode.HALF_UP);
            }

            PaymentScheduleItem item = PaymentScheduleItem.create(
                    schedule,
                    i,
                    firstPaymentDate.plusMonths(i - 1),
                    paymentAmountForMonth,
                    interestAmount,
                    principalAmount,
                    remainingPrincipal
            );
            items.add(item);
        }
        return items;
    }

    private void updateScheduleTotals(PaymentSchedule schedule, List<PaymentScheduleItem> items) {
        BigDecimal totalAmount = items.stream()
                .map(PaymentScheduleItem::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalInterest = items.stream()
                .map(PaymentScheduleItem::getInterestAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        schedule.setTotalAmount(totalAmount);
        schedule.setTotalInterest(totalInterest);
    }

    private PaymentSchedule getSchedule(Integer paymentScheduleId) {
        return scheduleRepo.findById(paymentScheduleId)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.SCHEDULE_NOT_FOUND, "Payment schedule not found"));
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal monthlyRate, int months) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal pow = onePlusR.pow(months);
        return principal
                .multiply(monthlyRate)
                .multiply(pow)
                .divide(pow.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }

    public PaymentScheduleResponseDTO getByLoanApplicationId(Integer loanApplicationId) {
        LoanApplication loan = loanRepo.findById(loanApplicationId)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.LOAN_NOT_FOUND, "Loan application not found"));

        PaymentSchedule schedule = scheduleRepo.findByLoanApplication(loan)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.SCHEDULE_NOT_FOUND, "Payment schedule not found for this loan application"));

        List<PaymentScheduleItem> items = scheduleItemRepo.findByPaymentSchedule(schedule);

        return PaymentScheduleMapper.toResponseDTO(schedule, items);
    }
}
