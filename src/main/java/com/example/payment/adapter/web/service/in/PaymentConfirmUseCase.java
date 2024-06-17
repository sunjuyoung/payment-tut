package com.example.payment.adapter.web.service.in;

import com.example.payment.adapter.web.domain.PaymentConfirmResult;

public interface PaymentConfirmUseCase {
    public PaymentConfirmResult confirm(PaymentConfirmCommand command);
}
