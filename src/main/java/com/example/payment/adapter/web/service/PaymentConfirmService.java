package com.example.payment.adapter.web.service;

import com.example.payment.adapter.web.domain.PaymentConfirmResult;
import com.example.payment.adapter.web.domain.PaymentOrder;
import com.example.payment.adapter.web.domain.PaymentOrderHistories;
import com.example.payment.adapter.web.domain.enums.PaymentOrderStatus;
import com.example.payment.adapter.web.repository.PaymentEventRepository;
import com.example.payment.adapter.web.repository.PaymentOrderHistoriesRepository;
import com.example.payment.adapter.web.repository.PaymentOrderRepository;
import com.example.payment.adapter.web.response.PaymentExecutionResult;
import com.example.payment.adapter.web.service.in.PaymentConfirmCommand;
import com.example.payment.adapter.web.service.in.PaymentConfirmUseCase;
import com.example.payment.common.exception.PaymentAlreadyException;
import com.example.payment.common.exception.PaymentValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentConfirmService implements PaymentConfirmUseCase {


    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentOrderHistoriesRepository paymentOrderHistoriesRepository;

    private final PaymentEventRepository  paymentEventRepository;

    private final TossPaymentService tossPaymentService;

    @Override
    public PaymentConfirmResult confirm(PaymentConfirmCommand command) {
        //1.상태 변경을 위한 조회
        List<PaymentOrder> paymentOrders = paymentOrderRepository.findByOrderId(command.getOrderId());

        //2. history 저장
        savePaymentOrderHistory(paymentOrders);

        //3. 상태 변경 저장
        updatePaymentOrderStatus(paymentOrders);

        //4. paymentKey 저장
        paymentEventRepository.updatePaymentKeyByOrderId(command.getOrderId(), command.getPaymentKey());

        log.info("--------------------------- 가격 검증 ---------------------------");
        log.info("주문번호 : " + command.getOrderId());
        log.info("주문번호 : " + command.getPaymentKey());
        log.info("결제 금액 : " + command.getAmount() + "원");
        //가격 검증
        isValid(command.getOrderId(), command.getAmount());

        //psp 결제 승인 요청 전달
        Mono<PaymentExecutionResult> execute = tossPaymentService.execute(command);

        //결제 승인 결과 저장
          execute.subscribe(paymentExecutionResult -> {
              log.info("결제 승인 결과 : " + paymentExecutionResult.getExtraDetails().getPspConfirmStatus().name());
                log.info("결제 승인 결과 : " + paymentExecutionResult.isSuccess());
                log.info("결제 승인 타입 : " + paymentExecutionResult.getExtraDetails().getType());
                log.info("결제 승인 메서드 : " + paymentExecutionResult.getExtraDetails().getMethod());
          });


        return null;
    }

    private static void updatePaymentOrderStatus(List<PaymentOrder> paymentOrders) {
        paymentOrders.stream().forEach(paymentOrder -> {
            if(paymentOrder.getPaymentOrderStatus() == PaymentOrderStatus.NOT_STARTED ||
                    paymentOrder.getPaymentOrderStatus() == PaymentOrderStatus.EXECUTING){
                paymentOrder.setPaymentStatus(PaymentOrderStatus.EXECUTING);
            }else  if(paymentOrder.getPaymentOrderStatus() == PaymentOrderStatus.SUCCESS) {
                throw new PaymentAlreadyException("이미 결제가 완료된 주문입니다.");
            } else if(paymentOrder.getPaymentOrderStatus() == PaymentOrderStatus.FAIL){
                throw new PaymentAlreadyException("이미 결제가 실패한 주문입니다.");
            }
        });
    }

    public void savePaymentOrderHistory(List<PaymentOrder> paymentOrders){
        paymentOrders.stream().forEach(paymentOrder -> {
            PaymentOrderHistories payment_confirm_start = new PaymentOrderHistories(paymentOrder,
                    paymentOrder.getPaymentOrderStatus(),
                    PaymentOrderStatus.EXECUTING,
                    "PAYMENT_CONFIRM_START");
            paymentOrderHistoriesRepository.save(payment_confirm_start);

        });
    }

    public Boolean isValid(String orderId, Long amount){
        //가격 검증
        Long value = paymentOrderRepository.sumAmountByOrderId(orderId);

        if(!amount.equals(value)){
            throw new PaymentValidationException(orderId + " : 결제 금액이 일치하지 않습니다. 금액: " + amount + "원, 결제 금액: " + value + "원");
        }
        return true;
    }



}
