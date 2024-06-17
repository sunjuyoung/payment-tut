package com.example.payment.adapter.web.domain.enums;

import lombok.Getter;

@Getter
public enum PaymentType {



    NORMAL("일반 결제");

    private String description;

    private PaymentType(String description) {
        this.description = description;
    }
}
