package com.example.payment.adapter.web.repository;

import com.example.payment.adapter.web.domain.PaymentEvent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent, Long> {


    @EntityGraph(attributePaths = "paymentOrders")
    Optional<PaymentEvent> findByOrderId(String orderId);

    //deleteByOrderId
    void deleteByOrderId(String orderId);


    //update  where orderId;
    @Modifying
    @Query("update PaymentEvent p set p.paymentKey = :paymentKey where p.orderId = :orderId")
    void updatePaymentKeyByOrderId(@Param("orderId") String orderId, @Param("paymentKey") String paymentKey);


}
