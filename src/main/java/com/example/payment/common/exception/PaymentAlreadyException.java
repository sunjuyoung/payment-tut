package com.example.payment.common.exception;

import lombok.Getter;

@Getter
public class PaymentAlreadyException extends RuntimeException{

    private String message;



    public PaymentAlreadyException(String message) {
        super(message);
    }
}
