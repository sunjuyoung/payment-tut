package com.example.payment.adapter.web.service.in;

import com.example.payment.adapter.web.domain.CheckoutResult;

public interface CheckOutUseCase {

    public CheckoutResult checkout(CheckoutCommand command);
}
