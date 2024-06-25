package com.example.payment.adapter.web.repository;

import com.example.payment.adapter.web.domain.PaymentEvent;
import com.example.payment.adapter.web.domain.enums.PaymentMethod;
import com.example.payment.adapter.web.domain.enums.PaymentType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent, Long> {


    @EntityGraph(attributePaths = "paymentOrders")
    Optional<PaymentEvent> findByOrderId(String orderId);

    //deleteByOrderId
    void deleteByOrderId(String orderId);


    //update  where orderId;
    @Modifying
    @Query("update PaymentEvent p set p.paymentKey = :paymentKey where p.orderId = :orderId")
    void updatePaymentKeyByOrderId( @Param("paymentKey") String paymentKey,@Param("orderId") String orderId);


    //update_payment_event_extra_details
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update PaymentEvent p " +
            " set p.orderName = :orderName, p.paymentMethod = :method, p.approvedAt =:approvedAt, p.paymentType =:type, p.pspRawData =:pspRawData, p.updatedAt = CURRENT_TIMESTAMP" +
            " where p.orderId = :orderId")
    void updatePaymentEventExtraDetails(@Param("orderId") String orderId,
                                        @Param("orderName") String orderName,
                                        @Param("method") PaymentMethod method,
                                        @Param("approvedAt") LocalDateTime approvedAt,
                                        @Param("type") PaymentType type,
                                        @Param("pspRawData") String pspRawData);


}
