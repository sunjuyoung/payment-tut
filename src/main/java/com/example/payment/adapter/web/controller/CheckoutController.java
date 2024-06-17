package com.example.payment.adapter.web.controller;

import com.example.payment.adapter.web.domain.CheckoutResult;
import com.example.payment.adapter.web.domain.PaymentConfirmResult;
import com.example.payment.adapter.web.request.CheckoutRequest;
import com.example.payment.adapter.web.request.TossPaymentConfirmRequest;
import com.example.payment.adapter.web.response.ApiResponse;
import com.example.payment.adapter.web.service.CheckoutService;
import com.example.payment.adapter.web.service.TossPaymentService;
import com.example.payment.adapter.web.service.in.CheckOutUseCase;
import com.example.payment.adapter.web.service.in.CheckoutCommand;
import com.example.payment.adapter.web.service.in.PaymentConfirmCommand;
import com.example.payment.adapter.web.service.in.PaymentConfirmUseCase;
import com.example.payment.common.IdempotencyCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/v1/toss")
@RequiredArgsConstructor
public class CheckoutController {

    private final TossPaymentService tossPaymentService;
    private final CheckOutUseCase checkOutUseCase;

    private final PaymentConfirmUseCase paymentConfirmUseCase;

    @GetMapping//@RequestBody CheckoutRequest request
    public Mono<String> checkoutPage( Model model) {

        CheckoutRequest request = new CheckoutRequest();
        request.setBuyerId(1L);
        request.setCartId(1L);
        request.setProductIds(List.of(1L, 2L, 3L));
        request.setSeed(UUID.randomUUID().toString().substring(0,8));

        CheckoutCommand checkoutCommand = CheckoutCommand.builder()
                .buyerId(request.getBuyerId())
                .cartId(request.getCartId())
                .productIds(request.getProductIds())
                .idempotencyKey(request.getSeed())
                .build();


        CheckoutResult checkout = checkOutUseCase.checkout(checkoutCommand);

        model.addAttribute("orderId", checkout.getOrderId());
        model.addAttribute("amount", checkout.getAmount());
        model.addAttribute("orderName", checkout.getOrderName());

        return Mono.just("checkout");
    }

    @RequestMapping(value = "/confirm")
    public ResponseEntity<ApiResponse<PaymentConfirmResult>> confirmPayment(@RequestBody TossPaymentConfirmRequest request) {

        PaymentConfirmCommand paymentConfirmCommand = new PaymentConfirmCommand(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );

        PaymentConfirmResult confirm = paymentConfirmUseCase.confirm(paymentConfirmCommand);
        return ResponseEntity.ok().body(new ApiResponse<>("success", HttpStatus.OK, confirm));

//        Mono<ResponseEntity<ApiResponse<String>>> success = tossPaymentService
//                .executePaymentConfirm(
//                        tossPaymentConfirmRequest.getPaymentKey(),
//                        tossPaymentConfirmRequest.getOrderId(),
//                        tossPaymentConfirmRequest.getAmount().toString()
//                )
//                .map(response -> {
//                    return ResponseEntity.ok().body(new ApiResponse<>("success", HttpStatus.OK, response));
//                });





     //   return success;
    }

}
