package com.maivel.loanApproval.controller;

import com.maivel.loanApproval.dto.PaymentScheduleResponseDTO;
import com.maivel.loanApproval.enums.RejectionReason;
import com.maivel.loanApproval.service.PaymentScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final PaymentScheduleService paymentScheduleService;

    public ScheduleController(PaymentScheduleService paymentScheduleService) {
        this.paymentScheduleService = paymentScheduleService;
    }

    @PostMapping("/loan/{loanApplicationId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Schedule created")
    public PaymentScheduleResponseDTO generateSchedule(@PathVariable Integer loanApplicationId) {
        return paymentScheduleService.generateSchedule(loanApplicationId);
    }

    @PostMapping("/{paymentScheduleId}/approve")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Schedule approved")
    public PaymentScheduleResponseDTO approveSchedule(@PathVariable Integer paymentScheduleId) {
        return paymentScheduleService.approveSchedule(paymentScheduleId);
    }

    @PostMapping("/{paymentScheduleId}/reject")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Schedule rejected")
    public PaymentScheduleResponseDTO rejectSchedule(
            @PathVariable Integer paymentScheduleId,
            @RequestParam RejectionReason reason) {
        return paymentScheduleService.rejectSchedule(paymentScheduleId, reason);
    }
    @GetMapping("/loan/{loanApplicationId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get loan application ID")
    public PaymentScheduleResponseDTO getByLoanApplicationId(@PathVariable Integer loanApplicationId) {
        return paymentScheduleService.getByLoanApplicationId(loanApplicationId);
    }
}
