package com.example.payment.adapter.web.response;

import com.example.payment.adapter.web.domain.enums.PSPConfirmStatus;
import com.example.payment.adapter.web.domain.enums.PaymentMethod;
import com.example.payment.adapter.web.domain.enums.PaymentType;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentExecutionResult {
    private String paymentKey;
    private String orderId;
    private PaymentExtraDetails extraDetails;
    private Failure failure;
    private boolean isSuccess;
    private boolean isFailure;
    private boolean isUnknown;
    private boolean isRetryable;




    @Getter @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public  static class PaymentExtraDetails {

        private PaymentType type;
        private PaymentMethod method;
        private LocalDateTime approvedAt;
        private String orderName;
        private PSPConfirmStatus pspConfirmStatus;
        private Long totalAmount;
        private String pspRawData; //승인결과
    }

    @Getter @Setter
    public  class Failure {
        private String code;
        private String message;

    }
}

