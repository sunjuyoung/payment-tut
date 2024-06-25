package com.example.payment.adapter.web.domain;

import com.example.payment.adapter.web.domain.enums.PaymentOrderStatus;
import com.example.payment.adapter.web.service.in.Failure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmResult {

    private PaymentOrderStatus status;
    private Failure failure;
    private String message;



    public void isValidMessage(){
        if(status == PaymentOrderStatus.SUCCESS){
          message = "결제 처리에 성공하였습니다.";
        }else if(status == PaymentOrderStatus.FAILURE) {
            message = "결제 처리에 실패하였습니다.";
        }
    }


}
