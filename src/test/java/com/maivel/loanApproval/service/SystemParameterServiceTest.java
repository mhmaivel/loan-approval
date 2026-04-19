package com.maivel.loanApproval.service;

import com.maivel.loanApproval.exception.NotFoundException;
import com.maivel.loanApproval.model.SystemParameter;
import com.maivel.loanApproval.repository.SystemParameterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemParameterServiceTest {

    @Mock
    private SystemParameterRepository parameterRepo;

    @InjectMocks
    private SystemParameterService systemParameterService;


    // getMaxCustomerAge()
    @Test
    void getMaxCustomerAge_parameterExists_returnsIntValue() {
        when(parameterRepo.findByParameterKey("MAX_CUSTOMER_AGE"))
                .thenReturn(Optional.of(buildParam("MAX_CUSTOMER_AGE", "70")));

        int result = systemParameterService.getMaxCustomerAge();

        assertThat(result).isEqualTo(70);
    }

    @Test
    void getMaxCustomerAge_parameterMissing_throwsNotFoundException() {
        when(parameterRepo.findByParameterKey("MAX_CUSTOMER_AGE"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> systemParameterService.getMaxCustomerAge())
                .isInstanceOf(NotFoundException.class);
    }


    // getBaseInterestRate6Month()
    @Test
    void getBaseInterestRate6Month_parameterExists_returnsBigDecimalValue() {
        when(parameterRepo.findByParameterKey("BASE_INTEREST_RATE_6M"))
                .thenReturn(Optional.of(buildParam("BASE_INTEREST_RATE_6M", "3.50")));

        BigDecimal result = systemParameterService.getBaseInterestRate6Month();

        assertThat(result).isEqualByComparingTo(new BigDecimal("3.50"));
    }

    @Test
    void getBaseInterestRate6Month_parameterMissing_throwsNotFoundException() {
        when(parameterRepo.findByParameterKey("BASE_INTEREST_RATE_6M"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> systemParameterService.getBaseInterestRate6Month())
                .isInstanceOf(NotFoundException.class);
    }


    // helper
    private SystemParameter buildParam(String key, String value) {
        SystemParameter param = new SystemParameter();
        param.setParameterKey(key);
        param.setParameterValue(value);
        return param;
    }
}
