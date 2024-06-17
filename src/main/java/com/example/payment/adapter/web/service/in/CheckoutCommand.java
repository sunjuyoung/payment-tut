package com.example.payment.adapter.web.service.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutCommand {

    //checkoutRequest 비슷하지만 멱등성을 보장하기 위한 키를 가진다, 오직 한번만 처리
    private Long cartId;
    private Long buyerId;
    private List<Long> productIds;
    private String idempotencyKey;



}
