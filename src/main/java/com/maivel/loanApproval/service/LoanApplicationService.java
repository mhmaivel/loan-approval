package com.maivel.loanApproval.service;

import com.maivel.loanApproval.dto.LoanRequestDTO;
import com.maivel.loanApproval.dto.LoanResponseDTO;
import com.maivel.loanApproval.enums.LoanStatus;
import com.maivel.loanApproval.enums.RejectionReason;
import com.maivel.loanApproval.exception.BusinessException;
import com.maivel.loanApproval.exception.ConflictException;
import com.maivel.loanApproval.exception.ErrorCodes;
import com.maivel.loanApproval.exception.InvalidStateException;
import com.maivel.loanApproval.mapper.LoanMapper;
import com.maivel.loanApproval.model.Customer;
import com.maivel.loanApproval.model.LoanApplication;
import com.maivel.loanApproval.repository.CustomerRepository;
import com.maivel.loanApproval.repository.LoanApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class LoanApplicationService {

    private final LoanApplicationRepository loanRepo;
    private final CustomerRepository customerRepo;
    private final SystemParameterService systemParameterService;

    public LoanApplicationService(LoanApplicationRepository loanRepo, CustomerRepository customerRepo, SystemParameterService systemParameterService) {
        this.loanRepo = loanRepo;
        this.customerRepo = customerRepo;
        this.systemParameterService = systemParameterService;

    }
    @Transactional
    public LoanResponseDTO createLoanResponse(LoanRequestDTO dto) {
        boolean hasActive = loanRepo.existsByCustomer_IdCodeAndStatusIn(
                dto.getIdCode(),
                List.of(LoanStatus.SUBMITTED, LoanStatus.IN_REVIEW));
        if (hasActive) {
            throw new ConflictException(ErrorCodes.ACTIVE_LOAN_EXISTS,
                    "Customer already has an active loan application");
        }

        Customer customer = customerRepo.findByIdCode(dto.getIdCode())
                .orElseGet(() -> customerRepo.save(LoanMapper.toCustomer(dto)));

        if (!customer.getFirstName().equals(dto.getFirstName()) || !customer.getLastName().equals(dto.getLastName())) {
            throw new ConflictException(ErrorCodes.CUSTOMER_NAME_EXISTS,
                    "Provided name does not match existing customer record");
        }

        BigDecimal baseInterestRate = systemParameterService.getBaseInterestRate6Month();
        LoanApplication loan = LoanMapper.toLoanApplication(dto, customer, baseInterestRate);

        int age = calculateAge(customer.getIdCode());
        if (age > systemParameterService.getMaxCustomerAge()) {
            loan.reject(RejectionReason.CUSTOMER_TOO_OLD);
        }

        loan = loanRepo.save(loan);
        return LoanMapper.toResponseDTO(customer, loan);
    }

    private int calculateAge(String idCode) {
        int century = Character.getNumericValue(idCode.charAt(0));
        int year = Integer.parseInt(idCode.substring(1, 3));
        int month = Integer.parseInt(idCode.substring(3, 5));
        int day = Integer.parseInt(idCode.substring(5, 7));

        int fullYear = switch (century){
            case 1, 2 -> 1800 + year;
            case 3, 4 -> 1900 + year;
            case 5, 6 -> 2000 + year;
            default -> throw new InvalidStateException(ErrorCodes.INVALID_ID_CODE, "Invalid idCode");
        };
        LocalDate birthDate = LocalDate.of(fullYear, month, day);
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
