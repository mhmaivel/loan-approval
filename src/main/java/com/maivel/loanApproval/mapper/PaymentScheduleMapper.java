package com.maivel.loanApproval.mapper;

import com.maivel.loanApproval.dto.PaymentScheduleItemDTO;
import com.maivel.loanApproval.dto.PaymentScheduleResponseDTO;
import com.maivel.loanApproval.model.PaymentSchedule;
import com.maivel.loanApproval.model.PaymentScheduleItem;

import java.util.List;

public class PaymentScheduleMapper {
    public static PaymentScheduleResponseDTO toResponseDTO(PaymentSchedule schedule, List<PaymentScheduleItem> items) {
        PaymentScheduleResponseDTO dto = new PaymentScheduleResponseDTO();
        dto.setFullName(schedule.getLoanApplication().getCustomer().getFirstName() + " " + schedule.getLoanApplication().getCustomer().getLastName());
        dto.setIdCode(schedule.getLoanApplication().getCustomer().getIdCode());

        dto.setLoanLengthMonths(schedule.getLoanApplication().getLoanLengthMonths());
        dto.setLoanAmount(schedule.getLoanApplication().getLoanAmount());
        dto.setInterestMargin(schedule.getLoanApplication().getInterestMargin());
        dto.setBaseInterestRate(schedule.getLoanApplication().getBaseInterestRate());

        dto.setPaymentScheduleId(schedule.getPaymentScheduleId());
        dto.setFirstPaymentDate(schedule.getFirstPaymentDate());
        dto.setLastPaymentDate(schedule.getLastPaymentDate());
        dto.setTotalAmount(schedule.getTotalAmount());
        dto.setTotalInterest(schedule.getTotalInterest());

        dto.setLoanStatus(schedule.getLoanApplication().getStatus());

        dto.setItems(items.stream().map(PaymentScheduleMapper::toItemDTO).toList());

        return dto;
    }

    public static PaymentScheduleItemDTO toItemDTO(PaymentScheduleItem item) {
        PaymentScheduleItemDTO dto = new PaymentScheduleItemDTO();
        dto.setInstallmentNumber(item.getInstallmentNumber());
        dto.setPaymentDate(item.getPaymentDate());
        dto.setPaymentAmount(item.getPaymentAmount());
        dto.setInterestAmount(item.getInterestAmount());
        dto.setPrincipalAmount(item.getPrincipalAmount());
        dto.setRemainingPrincipal(item.getRemainingPrincipal());
        return dto;
    }
}
