package com.example.payment.adapter.web.service;

import com.example.payment.adapter.web.domain.CheckoutResult;
import com.example.payment.adapter.web.repository.PaymentEventRepository;
import com.example.payment.adapter.web.service.in.CheckoutCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CheckoutServiceTest {

    @Autowired
    PaymentEventRepository paymentEventRepository;

    @Autowired
    private CheckoutService checkoutService;

    @Test
    @Transactional
    void checkout() {

        String orderId  = UUID.randomUUID().toString();
        CheckoutCommand command = CheckoutCommand.builder()
                .cartId(1L)
                .buyerId(1L)
                .productIds(List.of(1L, 2L, 3L))
                .idempotencyKey(orderId)
                .build();



        CheckoutResult checkout = checkoutService.checkout(command);

        assertNotNull(checkout);
        //3000원
        assertEquals(3000, checkout.getAmount());


        paymentEventRepository.findByOrderId(orderId)
                .ifPresent(paymentEvent -> {
                    assertEquals(orderId, paymentEvent.getOrderId());
                    assertEquals(3000, paymentEvent.getTotalAmount());
                    assertEquals(3, paymentEvent.getPaymentOrders().size());
                    assertFalse(paymentEvent.isPaymentDone());
                    paymentEvent.getPaymentOrders().forEach(paymentOrder -> {
                        assertEquals(orderId, paymentOrder.getOrderId());
                        assertFalse(paymentOrder.isLedgerUpdated());
                        assertFalse(paymentOrder.isWalletUpdated());
                    });
                });
    }

    @Test
    @DisplayName("연속으로 두번 결제 요청시 중복 결제 방지")
    @Transactional
    @Rollback(value = true)
    void checkout2(){
        String orderId  = UUID.randomUUID().toString();
        CheckoutCommand command = CheckoutCommand.builder()
                .cartId(1L)
                .buyerId(1L)
                .productIds(List.of(1L, 2L, 3L))
                .idempotencyKey(orderId)
                .build();
         checkoutService.checkout(command);
         assertThrows(IllegalArgumentException.class, ()->checkoutService.checkout(command));

    }

    @Test
    @Transactional
    @Rollback(value = false)
    void deletePaymentEvent() {
        String orderId  = UUID.randomUUID().toString();
        CheckoutCommand command = CheckoutCommand.builder()
                .cartId(1L)
                .buyerId(1L)
                .productIds(List.of(1L, 2L, 3L))
                .idempotencyKey(orderId)
                .build();
        checkoutService.checkout(command);
        paymentEventRepository.deleteByOrderId(orderId);
    }


}