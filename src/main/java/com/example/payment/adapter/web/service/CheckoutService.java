package com.example.payment.adapter.web.service;

import com.example.payment.adapter.web.domain.*;
import com.example.payment.adapter.web.domain.enums.PaymentOrderStatus;
import com.example.payment.adapter.web.repository.PaymentEventRepository;
import com.example.payment.adapter.web.repository.ProductRepository;
import com.example.payment.adapter.web.service.in.CheckOutUseCase;
import com.example.payment.adapter.web.service.in.CheckoutCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

//usecase 핵심 기능 작업 흐름
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CheckoutService implements CheckOutUseCase {

    private final ProductRepository productRepository;
    private final PaymentEventRepository paymentEventRepository;


    @Override
    public CheckoutResult checkout(CheckoutCommand command) {

        paymentEventRepository.findByOrderId(command.getIdempotencyKey())
                .ifPresent(paymentEvent -> {
                    throw new IllegalArgumentException("이미 결제가 진행중인 주문입니다.");
                });

        List<Product> products = productRepository.findAllById(command.getProductIds());


        PaymentEvent paymentEvent = createPaymentEvent(command, products);

        PaymentEvent newPayment = paymentEventRepository.save(paymentEvent);

        CheckoutResult checkoutResult = CheckoutResult.builder()
                .amount(newPayment.getTotalAmount())
                .orderId(newPayment.getOrderId())
                .orderName(newPayment.getOrderName())
                .build();

        return checkoutResult;
    }

    private PaymentEvent createPaymentEvent(CheckoutCommand command, List<Product> products){

        Long TotalAmount = products.stream().mapToLong(Product::getAmount).sum();
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .buyerId(command.getBuyerId())
                .orderId(command.getIdempotencyKey())
                .orderName(products.stream().map(Product::getName).collect(Collectors.joining(", ")) )
                .totalAmount(TotalAmount)
                .build();

        List<PaymentOrder> paymentOrders = products.stream().map(product -> {
            PaymentOrder build = PaymentOrder.builder()
                    .product(product)
                    .orderId(command.getIdempotencyKey())
                    .sellerId(product.getSellerId())
                    .amount(product.getAmount())
                    .buyerId(command.getBuyerId())
                    .paymentOrderStatus(PaymentOrderStatus.NOT_STARTED)
                    .build();
            return build;
        }).collect(Collectors.toList());

        paymentOrders.forEach(paymentOrder -> {
            paymentEvent.addPaymentOrder(paymentOrder);
        });

        return paymentEvent;
    }
}
