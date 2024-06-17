package com.example.payment.common.exception;

import lombok.Getter;

@Getter
public class PaymentValidationException extends RuntimeException{

    private String message;


    public PaymentValidationException(String message) {
        super(message);
    }

}
