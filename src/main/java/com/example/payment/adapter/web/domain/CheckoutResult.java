package com.example.payment.adapter.web.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutResult {

    private Long amount;
    private String orderId;
    private String orderName;

}
