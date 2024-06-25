package com.example.payment.adapter.web.service.in;

import com.example.payment.adapter.web.domain.enums.PSPConfirmStatus;
import com.example.payment.adapter.web.domain.enums.PaymentMethod;
import com.example.payment.adapter.web.domain.enums.PaymentOrderStatus;
import com.example.payment.adapter.web.domain.enums.PaymentType;
import com.example.payment.adapter.web.response.PaymentExecutionResult;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusUpdateCommand {
    private String paymentKey;
    private String orderId;
    private PaymentOrderStatus status;
    private PaymentExtraDetails extraDetails;
    private Failure failure;


    //valid PaymentOrderStatus값이 success 라면  extraDetails는 null이 아니어야 한다.
    public boolean isValid(){
        return status == PaymentOrderStatus.SUCCESS ? extraDetails != null : true;
    }
    //valid PaymentOrderStatus값이 failure 라면  failure는 null이 아니어야 한다.
    public boolean isValidFailure(){
        return status == PaymentOrderStatus.FAILURE ? failure != null : true;
    }






}
