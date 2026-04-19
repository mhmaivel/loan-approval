package com.maivel.loanApproval.repository;

import com.maivel.loanApproval.model.LoanApplication;
import com.maivel.loanApproval.model.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Integer> {
    boolean existsByLoanApplication(LoanApplication loanApplication);

    Optional<PaymentSchedule> findByLoanApplication(LoanApplication loan);
}
