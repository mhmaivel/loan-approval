package com.maivel.loanApproval.service;

import com.maivel.loanApproval.dto.LoanRequestDTO;
import com.maivel.loanApproval.dto.LoanResponseDTO;
import com.maivel.loanApproval.enums.LoanStatus;
import com.maivel.loanApproval.enums.RejectionReason;
import com.maivel.loanApproval.exception.ConflictException;
import com.maivel.loanApproval.model.Customer;
import com.maivel.loanApproval.model.LoanApplication;
import com.maivel.loanApproval.repository.CustomerRepository;
import com.maivel.loanApproval.repository.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository loanRepo;

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private SystemParameterService systemParameterService;

    @InjectMocks
    private LoanApplicationService loanApplicationService;

    // 38001085718 → born 08/01/1980, 45
    private static final String YOUNG_ID_CODE = "38001085718";
    // 31001085718 → born 08/01/1910 1910-01-08, 115
    private static final String OLD_ID_CODE   = "31001085718";

    private static final int MAX_CUSTOMER_AGE = 70;

    private LoanRequestDTO youngDto;
    private LoanRequestDTO oldDto;

    @BeforeEach
    void setUp() {
        youngDto = new LoanRequestDTO(
                "Mari", "Maasikas", YOUNG_ID_CODE,
                24, new BigDecimal("2.00"), new BigDecimal("10000")
        );

        oldDto = new LoanRequestDTO(
                "Jaan", "Vana", OLD_ID_CODE,
                24, new BigDecimal("2.00"), new BigDecimal("10000")
        );
    }


    //Create new application
    @Test
    void createLoanResponse_newCustomer_createsApplicationWithSubmittedStatus() {
        when(systemParameterService.getMaxCustomerAge()).thenReturn(MAX_CUSTOMER_AGE);
        when(loanRepo.existsByCustomer_IdCodeAndStatusIn(eq(YOUNG_ID_CODE), any())).thenReturn(false);
        when(customerRepo.findByIdCode(YOUNG_ID_CODE)).thenReturn(Optional.empty());

        Customer savedCustomer = buildCustomer(YOUNG_ID_CODE, "Mari", "Maasikas");
        when(customerRepo.save(any(Customer.class))).thenReturn(savedCustomer);
        when(loanRepo.save(any(LoanApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanResponseDTO result = loanApplicationService.createLoanResponse(youngDto);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.SUBMITTED);
        assertThat(result.getRejectionReason()).isNull();
        verify(customerRepo).save(any(Customer.class));
        verify(loanRepo).save(any(LoanApplication.class));
    }


    //Error if active application exists
    @Test
    void createLoanResponse_activeApplicationExists_throwsConflictException() {
        when(loanRepo.existsByCustomer_IdCodeAndStatusIn(eq(YOUNG_ID_CODE), any())).thenReturn(true);

        assertThatThrownBy(() -> loanApplicationService.createLoanResponse(youngDto))
                .isInstanceOf(ConflictException.class);

        verify(loanRepo, never()).save(any());
    }


    //Rejected if customer too old
    @Test
    void createLoanResponse_customerTooOld_savesApplicationAsRejected() {
        when(systemParameterService.getMaxCustomerAge()).thenReturn(MAX_CUSTOMER_AGE);
        when(loanRepo.existsByCustomer_IdCodeAndStatusIn(eq(OLD_ID_CODE), any())).thenReturn(false);

        Customer oldCustomer = buildCustomer(OLD_ID_CODE, "Jaan", "Vana");
        when(customerRepo.findByIdCode(OLD_ID_CODE)).thenReturn(Optional.of(oldCustomer));
        when(loanRepo.save(any(LoanApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanResponseDTO result = loanApplicationService.createLoanResponse(oldDto);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.REJECTED);
        assertThat(result.getRejectionReason()).isEqualTo(RejectionReason.CUSTOMER_TOO_OLD);
    }


    //Delete customer if they exists
    @Test
    void createLoanResponse_existingCustomer_doesNotCreateNewCustomer() {
        when(systemParameterService.getMaxCustomerAge()).thenReturn(MAX_CUSTOMER_AGE);
        when(loanRepo.existsByCustomer_IdCodeAndStatusIn(eq(YOUNG_ID_CODE), any())).thenReturn(false);

        Customer existing = buildCustomer(YOUNG_ID_CODE, "Mari", "Maasikas");
        when(customerRepo.findByIdCode(YOUNG_ID_CODE)).thenReturn(Optional.of(existing));
        when(loanRepo.save(any(LoanApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        loanApplicationService.createLoanResponse(youngDto);

        verify(customerRepo, never()).save(any());
    }


    //Creates new customer if they do not exist
    @Test
    void createLoanResponse_noExistingCustomer_createsNewCustomer() {
        when(systemParameterService.getMaxCustomerAge()).thenReturn(MAX_CUSTOMER_AGE);
        when(loanRepo.existsByCustomer_IdCodeAndStatusIn(eq(YOUNG_ID_CODE), any())).thenReturn(false);
        when(customerRepo.findByIdCode(YOUNG_ID_CODE)).thenReturn(Optional.empty());

        Customer newCustomer = buildCustomer(YOUNG_ID_CODE, "Mari", "Maasikas");
        when(customerRepo.save(any(Customer.class))).thenReturn(newCustomer);
        when(loanRepo.save(any(LoanApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        loanApplicationService.createLoanResponse(youngDto);

        verify(customerRepo).save(any(Customer.class));
    }


    // helper
    private Customer buildCustomer(String idCode, String firstName, String lastName) {
        Customer c = new Customer();
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setIdCode(idCode);
        return c;
    }
}
