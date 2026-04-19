package com.maivel.loanApproval.controller;

import com.maivel.loanApproval.dto.LoanRequestDTO;
import com.maivel.loanApproval.dto.LoanResponseDTO;
import com.maivel.loanApproval.service.LoanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanApplicationService loanApplicationService;

    public LoanController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Loan created")
    public LoanResponseDTO createLoanApplication(@Valid @RequestBody LoanRequestDTO dto) {
        return loanApplicationService.createLoanResponse(dto);
    }
}
