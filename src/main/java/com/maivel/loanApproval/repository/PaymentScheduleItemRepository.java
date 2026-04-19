package com.maivel.loanApproval.repository;
import com.maivel.loanApproval.model.PaymentSchedule;
import com.maivel.loanApproval.model.PaymentScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentScheduleItemRepository extends JpaRepository<PaymentScheduleItem, Integer> {
    List<PaymentScheduleItem> findByPaymentSchedule(PaymentSchedule paymentSchedule);
}
