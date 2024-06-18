package com.example.payment.adapter.web.domain.enums;

import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
public enum PaymentMethod {

    //결제 방법, 카드,간편결제,휴대폰결제
    CARD("카드"),
    EASY_PAY("간편결제");

    //간편결제




    private String description;

    private PaymentMethod(String description) {
        this.description = description;
    }

    public static PaymentMethod getPaymentMethod(String description){
        return description.equals("간편결제") ? PaymentMethod.EASY_PAY : null;
    }
}
