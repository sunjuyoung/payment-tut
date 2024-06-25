package com.example.payment.adapter.web.response;

import com.example.payment.adapter.web.domain.enums.PSPConfirmStatus;
import com.example.payment.adapter.web.domain.enums.PaymentMethod;
import com.example.payment.adapter.web.domain.enums.PaymentOrderStatus;
import com.example.payment.adapter.web.domain.enums.PaymentType;
import com.example.payment.adapter.web.service.in.Failure;
import com.example.payment.adapter.web.service.in.PaymentExtraDetails;
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

    public PaymentOrderStatus paymentOrderStatus() {
        if (isSuccess()) {
            return PaymentOrderStatus.SUCCESS;
        } else if (isFailure()) {
            return PaymentOrderStatus.FAILURE;
        } else {
            throw new IllegalStateException("결제 (orderId: " + getOrderId() + ") 는 올바르지 않은 결제 상태입니다.");
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
    public boolean isFailure() {
        return isFailure;
    }

}

