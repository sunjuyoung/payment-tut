package com.example.payment.adapter.web.service.in;

import com.example.payment.adapter.web.domain.enums.PSPConfirmStatus;
import com.example.payment.adapter.web.domain.enums.PaymentMethod;
import com.example.payment.adapter.web.domain.enums.PaymentType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public  class PaymentExtraDetails {

    private PaymentType type;
    private PaymentMethod method;
    private LocalDateTime approvedAt;
    private String orderName;
    private PSPConfirmStatus pspConfirmStatus;
    private Long totalAmount;
    private String pspRawData; //승인결과
}