package com.maivel.loanApproval.service;

import com.maivel.loanApproval.dto.PaymentScheduleResponseDTO;
import com.maivel.loanApproval.enums.LoanStatus;
import com.maivel.loanApproval.enums.RejectionReason;
import com.maivel.loanApproval.exception.ConflictException;
import com.maivel.loanApproval.exception.InvalidStateException;
import com.maivel.loanApproval.exception.NotFoundException;
import com.maivel.loanApproval.model.Customer;
import com.maivel.loanApproval.model.LoanApplication;
import com.maivel.loanApproval.model.PaymentSchedule;
import com.maivel.loanApproval.repository.LoanApplicationRepository;
import com.maivel.loanApproval.repository.PaymentScheduleItemRepository;
import com.maivel.loanApproval.repository.PaymentScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentScheduleServiceTest {

    @Mock
    private PaymentScheduleRepository scheduleRepo;

    @Mock
    private PaymentScheduleItemRepository scheduleItemRepo;

    @Mock
    private LoanApplicationRepository loanRepo;

    @InjectMocks
    private PaymentScheduleService paymentScheduleService;

    private Customer customer;
    private LoanApplication submittedLoan;
    private LoanApplication inReviewLoan;
    private LoanApplication approvedLoan;
    private LoanApplication rejectedLoan;

    // Test data to check the annuity calculation
    // P=10000, annual rate=5.5% (margin 2 + base 3.5), n=24
    // monthly rate r = 5.5/1200 = 0.00458333...
    // M = 10000 * r*(1+r)^24 / ((1+r)^24 - 1) ≈ 440.96
    private static final BigDecimal PRINCIPAL = new BigDecimal("10000");
    private static final BigDecimal INTEREST_MARGIN = new BigDecimal("2.00");
    private static final BigDecimal BASE_RATE = new BigDecimal("3.50");
    private static final int MONTHS = 24;
    private static final BigDecimal EXPECTED_MONTHLY_PAYMENT = new BigDecimal("440.96");

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setFirstName("Mari");
        customer.setLastName("Maasikas");
        customer.setIdCode("38001085718");

        submittedLoan = LoanApplication.create(customer, MONTHS, PRINCIPAL, INTEREST_MARGIN, BASE_RATE);

        inReviewLoan = LoanApplication.create(customer, MONTHS, PRINCIPAL, INTEREST_MARGIN, BASE_RATE);
        inReviewLoan.moveToInReview();

        approvedLoan = LoanApplication.create(customer, MONTHS, PRINCIPAL, INTEREST_MARGIN, BASE_RATE);
        approvedLoan.moveToInReview();
        approvedLoan.approve();

        rejectedLoan = LoanApplication.create(customer, MONTHS, PRINCIPAL, INTEREST_MARGIN, BASE_RATE);
        rejectedLoan.reject(RejectionReason.CUSTOMER_TOO_OLD);
    }


    //Generates schedule successfully and moves status to IN_REVIEW
    @Test
    void generateSchedule_validSubmittedLoan_savesScheduleAndMovesToInReview() {
        when(loanRepo.findById(1)).thenReturn(Optional.of(submittedLoan));
        when(scheduleRepo.existsByLoanApplication(submittedLoan)).thenReturn(false);
        when(scheduleRepo.save(any(PaymentSchedule.class))).thenAnswer(inv -> inv.getArgument(0));
        when(scheduleItemRepo.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
        when(loanRepo.save(any(LoanApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentScheduleResponseDTO result = paymentScheduleService.generateSchedule(1);

        assertThat(submittedLoan.getStatus()).isEqualTo(LoanStatus.IN_REVIEW);
        assertThat(result.getItems()).hasSize(MONTHS);
        assertThat(result.getTotalInterest()).isNotNull();
        // Kontrollib, et scheduleRepo.save() kutsuti vähemalt korra (sisemist arvu ei fikseerita)
        verify(scheduleRepo, atLeastOnce()).save(any(PaymentSchedule.class));
        verify(loanRepo).save(submittedLoan);
    }


    //Accuracy of annuity calculation
    @Test
    void generateSchedule_annuityCalculation_firstMonthlyPaymentMatchesExpected() {
        when(loanRepo.findById(1)).thenReturn(Optional.of(submittedLoan));
        when(scheduleRepo.existsByLoanApplication(submittedLoan)).thenReturn(false);
        when(scheduleRepo.save(any(PaymentSchedule.class))).thenAnswer(inv -> inv.getArgument(0));
        when(scheduleItemRepo.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
        when(loanRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentScheduleResponseDTO result = paymentScheduleService.generateSchedule(1);

        // Esimene makse (mitte viimane korrigeeritud) peaks vastama käsitsi arvutusele
        BigDecimal firstPayment = result.getItems().get(0).getPaymentAmount();
        assertThat(firstPayment).isEqualByComparingTo(EXPECTED_MONTHLY_PAYMENT);
    }


    //Do not allow generation if the request is in the wrong status
    @Test
    void generateSchedule_approvedLoan_throwsInvalidStateException() {
        when(loanRepo.findById(2)).thenReturn(Optional.of(approvedLoan));

        assertThatThrownBy(() -> paymentScheduleService.generateSchedule(2))
                .isInstanceOf(InvalidStateException.class);

        verify(scheduleRepo, never()).save(any());
    }

    @Test
    void generateSchedule_rejectedLoan_throwsInvalidStateException() {
        when(loanRepo.findById(3)).thenReturn(Optional.of(rejectedLoan));

        assertThatThrownBy(() -> paymentScheduleService.generateSchedule(3))
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    void generateSchedule_inReviewLoan_throwsInvalidStateException() {
        when(loanRepo.findById(4)).thenReturn(Optional.of(inReviewLoan));

        assertThatThrownBy(() -> paymentScheduleService.generateSchedule(4))
                .isInstanceOf(InvalidStateException.class);
    }


    //Does not allow creating duplicate schedules
    @Test
    void generateSchedule_scheduleAlreadyExists_throwsConflictException() {
        when(loanRepo.findById(1)).thenReturn(Optional.of(submittedLoan));
        when(scheduleRepo.existsByLoanApplication(submittedLoan)).thenReturn(true);

        assertThatThrownBy(() -> paymentScheduleService.generateSchedule(1))
                .isInstanceOf(ConflictException.class);

        verify(scheduleRepo, never()).save(any());
    }



    //Loan not found
    @Test
    void generateSchedule_loanNotFound_throwsNotFoundException() {
        when(loanRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentScheduleService.generateSchedule(99))
                .isInstanceOf(NotFoundException.class);
    }


    //Approve work only in status IN_REVIEW
    @Test
    void approveSchedule_inReviewLoan_approvesSuccessfully() {
        PaymentSchedule schedule = buildSchedule(inReviewLoan);
        when(scheduleRepo.findById(1)).thenReturn(Optional.of(schedule));
        when(scheduleItemRepo.findByPaymentSchedule(schedule)).thenReturn(Collections.emptyList());
        when(loanRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentScheduleResponseDTO result = paymentScheduleService.approveSchedule(1);

        assertThat(inReviewLoan.getStatus()).isEqualTo(LoanStatus.APPROVED);
        assertThat(result.getItems()).isNotNull();
    }

    @Test
    void approveSchedule_approvedLoan_throwsInvalidStateException() {
        PaymentSchedule schedule = buildSchedule(approvedLoan);
        when(scheduleRepo.findById(1)).thenReturn(Optional.of(schedule));

        assertThatThrownBy(() -> paymentScheduleService.approveSchedule(1))
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    void approveSchedule_submittedLoan_throwsInvalidStateException() {
        PaymentSchedule schedule = buildSchedule(submittedLoan);
        when(scheduleRepo.findById(1)).thenReturn(Optional.of(schedule));

        assertThatThrownBy(() -> paymentScheduleService.approveSchedule(1))
                .isInstanceOf(InvalidStateException.class);
    }


    //Reject work only in status IN_REVIEW
    @Test
    void rejectSchedule_inReviewLoan_rejectsSuccessfully() {
        PaymentSchedule schedule = buildSchedule(inReviewLoan);
        when(scheduleRepo.findById(1)).thenReturn(Optional.of(schedule));
        when(scheduleItemRepo.findByPaymentSchedule(schedule)).thenReturn(Collections.emptyList());
        when(loanRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentScheduleResponseDTO result = paymentScheduleService.rejectSchedule(1, RejectionReason.CUSTOMER_TOO_OLD);

        assertThat(inReviewLoan.getStatus()).isEqualTo(LoanStatus.REJECTED);
        assertThat(inReviewLoan.getRejectionReason()).isEqualTo(RejectionReason.CUSTOMER_TOO_OLD);
        assertThat(result.getItems()).isNotNull();
    }

    @Test
    void rejectSchedule_submittedLoan_throwsInvalidStateException() {
        PaymentSchedule schedule = buildSchedule(submittedLoan);
        when(scheduleRepo.findById(1)).thenReturn(Optional.of(schedule));

        assertThatThrownBy(() -> paymentScheduleService.rejectSchedule(1, RejectionReason.CUSTOMER_TOO_OLD))
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    void rejectSchedule_scheduleNotFound_throwsNotFoundException() {
        when(scheduleRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentScheduleService.rejectSchedule(99, RejectionReason.CUSTOMER_TOO_OLD))
                .isInstanceOf(NotFoundException.class);
    }

    // helper
    private PaymentSchedule buildSchedule(LoanApplication loan) {
        return PaymentSchedule.create(
                loan,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(MONTHS - 1),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }
}
