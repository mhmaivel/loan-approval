package com.maivel.loanApproval.mapper;

import com.maivel.loanApproval.dto.LoanRequestDTO;
import com.maivel.loanApproval.dto.LoanResponseDTO;
import com.maivel.loanApproval.model.Customer;
import com.maivel.loanApproval.model.LoanApplication;

import java.math.BigDecimal;

public class LoanMapper {
    public static LoanResponseDTO toResponseDTO(Customer customer, LoanApplication loan) {
        LoanResponseDTO dto = new LoanResponseDTO();
        dto.setLoanApplicationId(loan.getLoanApplicationId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setIdCode(customer.getIdCode());
        dto.setLoanLengthMonths(loan.getLoanLengthMonths());
        dto.setInterestMargin(loan.getInterestMargin());
        dto.setBaseInterestRate(loan.getBaseInterestRate());
        dto.setLoanAmount(loan.getLoanAmount());
        dto.setStatus(loan.getStatus());
        dto.setRejectionReason(loan.getRejectionReason());
        return dto;
    }

    public static Customer toCustomer(LoanRequestDTO dto) {
        return Customer.create(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getIdCode());
    }

    public static LoanApplication toLoanApplication(LoanRequestDTO dto, Customer customer, BigDecimal baseInterestRate) {
        return LoanApplication.create(
                customer,
                dto.getLoanLengthMonths(),
                dto.getLoanAmount(),
                dto.getInterestMargin(),
                baseInterestRate);
    }
}
