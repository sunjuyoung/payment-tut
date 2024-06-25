package com.example.payment.adapter.web.repository;

import com.example.payment.adapter.web.domain.PaymentOrder;
import com.example.payment.adapter.web.domain.enums.PaymentOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {

    List<PaymentOrder> findByOrderId(String orderId);

    //sum(amount) where orderId
    @Query("select sum(p.amount) as total_amount  from PaymentOrder p where p.orderId = :orderId")
    Long sumAmountByOrderId(String orderId);


    @Modifying
    @Query("update PaymentOrder p set p.paymentOrderStatus =:status, p.updatedAt = CURRENT_TIMESTAMP" +
            " where p.orderId = :orderId")
    void updatePaymentOrderStatusByOrderId(@Param("orderId") String orderId, @Param("status")PaymentOrderStatus status);
}
