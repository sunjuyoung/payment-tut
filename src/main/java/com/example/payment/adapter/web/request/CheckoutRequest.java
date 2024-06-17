package com.example.payment.adapter.web.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CheckoutRequest {

    private Long cartId;
    private List<Long> productIds;
    private Long buyerId;
    private String seed;


    public CheckoutRequest() {
        this.cartId = 1L;
        this.productIds = List.of(1L, 2L, 3L);
        this.buyerId = 1L;
        this.seed = LocalDateTime.now().toString();
    }


}
