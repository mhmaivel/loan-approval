package com.maivel.loanApproval.repository;
import com.maivel.loanApproval.enums.LoanStatus;
import com.maivel.loanApproval.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Integer> {
    boolean existsByCustomer_IdCodeAndStatusIn(String idCode, List<LoanStatus> status);
}
