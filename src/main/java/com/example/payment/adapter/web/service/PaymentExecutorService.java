package com.example.payment.adapter.web.service;

import com.example.payment.adapter.web.response.PaymentExecutionResult;
import com.example.payment.adapter.web.service.in.PaymentConfirmCommand;
import org.springframework.stereotype.Service;

@Service
public class PaymentExecutorService {

    public PaymentExecutionResult execute(PaymentConfirmCommand command) {


        return new PaymentExecutionResult();
    }
}
