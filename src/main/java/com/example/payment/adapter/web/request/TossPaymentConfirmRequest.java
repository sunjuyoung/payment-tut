package com.example.payment.adapter.web.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TossPaymentConfirmRequest {

    private String paymentKey;
    private String orderId;
    private Long amount;


}
