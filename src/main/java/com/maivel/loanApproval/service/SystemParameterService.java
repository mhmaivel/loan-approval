package com.maivel.loanApproval.service;

import com.maivel.loanApproval.exception.ConfigurationException;
import com.maivel.loanApproval.exception.ErrorCodes;
import com.maivel.loanApproval.exception.NotFoundException;
import com.maivel.loanApproval.repository.SystemParameterRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SystemParameterService {

    private final SystemParameterRepository parameterRepo;

    public SystemParameterService(SystemParameterRepository parameterRepo) {
        this.parameterRepo = parameterRepo;
    }

    public int getMaxCustomerAge() {
        return parseInteger("MAX_CUSTOMER_AGE");
    }

    public BigDecimal getBaseInterestRate6Month() {
        return parseBigDecimal("BASE_INTEREST_RATE_6M");
    }

    private int parseInteger(String key) {
        String raw = getParameter(key);
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(
                    ErrorCodes.INVALID_PARAMETER_FORMAT,
                    "System parameter '" + key + "' has invalid integer value: '" + raw + "'"
            );
        }
    }

    private BigDecimal parseBigDecimal(String key) {
        String raw = getParameter(key);
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(
                    ErrorCodes.INVALID_PARAMETER_FORMAT,
                    "System parameter '" + key + "' has invalid decimal value: '" + raw + "'"
            );
        }
    }

    private String getParameter(String key) {
        return parameterRepo.findByParameterKey(key)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.PARAMETER_NOT_FOUND, key))
                .getParameterValue();
    }
}
