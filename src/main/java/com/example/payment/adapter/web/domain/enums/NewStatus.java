package com.example.payment.adapter.web.domain.enums;

import lombok.Getter;

@Getter
public enum NewStatus {


    NOT_STARTED("결제 시작 전"),
    EXECUTING("결제 진행 중"),
    SUCCESS("결제 성공"),
    FAIL("결제 실패");

    private String description;

    private NewStatus(String description) {
        this.description = description;
    }
}
