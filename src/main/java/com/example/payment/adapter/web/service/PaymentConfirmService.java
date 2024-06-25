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
import com.example.payment.adapter.web.service.in.PaymentStatusUpdateCommand;
import com.example.payment.common.exception.PaymentAlreadyException;
import com.example.payment.common.exception.PaymentValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;

    @Override
    public PaymentConfirmResult confirm(PaymentConfirmCommand command) {
        //1.상태 변경을 위한 조회
        List<PaymentOrder> paymentOrders = paymentOrderRepository.findByOrderId(command.getOrderId());

        //2. history 저장
        savePaymentOrderHistory(paymentOrders);

        //3. 상태 변경 저장
        updatePaymentOrderStatus(paymentOrders);

        //4. paymentKey 저장
        paymentEventRepository.updatePaymentKeyByOrderId(command.getPaymentKey(),command.getOrderId());

        //가격 검증
        isValid(command.getOrderId(), command.getAmount());

        //psp 결제 승인 요청 전달
        PaymentExecutionResult res = tossPaymentService.execute(command).block();
        //결제 승인 결과 따른 저장

        PaymentStatusUpdateCommand paymentStatusUpdateCommand
                    = PaymentStatusUpdateCommand.builder()
                    .paymentKey(res.getPaymentKey())
                    .orderId(res.getOrderId())
                    .status(res.paymentOrderStatus())
                    .extraDetails(res.getExtraDetails())
                    .failure(res.getFailure())
                    .build();

        updatePaymentStatus(paymentStatusUpdateCommand);

        PaymentConfirmResult confirmResult = PaymentConfirmResult.builder()
                    .status(res.paymentOrderStatus())
                    .failure(res.getFailure())
                    .build();
            confirmResult.isValidMessage();
            return confirmResult;

//        return execute.map(res -> {
//            PaymentStatusUpdateCommand paymentStatusUpdateCommand
//                    = PaymentStatusUpdateCommand.builder()
//                    .paymentKey(res.getPaymentKey())
//                    .orderId(res.getOrderId())
//                    .status(res.paymentOrderStatus())
//                    .extraDetails(res.getExtraDetails())
//                    .failure(res.getFailure())
//                    .build();
//            updatePaymentStatus(paymentStatusUpdateCommand);
//            PaymentConfirmResult confirmResult = PaymentConfirmResult.builder()
//                    .status(res.paymentOrderStatus())
//                    .failure(res.getFailure())
//                    .build();
//            confirmResult.isValidMessage();
//            return confirmResult;
//        }).block();
    }


    //결제 승인 결과 따른 저장
    public boolean updatePaymentStatus(PaymentStatusUpdateCommand command){
        if(command.getStatus().equals(PaymentOrderStatus.SUCCESS)) {
           return updatePaymentStatusToSuccess(command);
        }else if(command.getStatus().equals(PaymentOrderStatus.FAILURE)){
            return updatePaymentStatusToFailure(command);
        }else {
            throw new IllegalArgumentException("결제 상태가 올바르지 않습니다, 상태:"+ command.getStatus());
        }

    }

    //결제 실패
    public boolean updatePaymentStatusToFailure(PaymentStatusUpdateCommand command) {
        List<PaymentOrder> paymentOrderList = paymentOrderRepository.findByOrderId(command.getOrderId());
        insertPaymentHistory(paymentOrderList, command.getStatus(),command.getStatus().toString());
        paymentOrderRepository.updatePaymentOrderStatusByOrderId(command.getOrderId(), command.getStatus());

        return true;
    }

    //결제 성공
    public boolean updatePaymentStatusToSuccess(PaymentStatusUpdateCommand command){
        //select paymentOrder
        List<PaymentOrder> paymentOrderList = paymentOrderRepository.findByOrderId(command.getOrderId());
        //history 저장
        insertPaymentHistory(paymentOrderList, command.getStatus(),"PAYMENT_CONFIRMATION_DONE");
        //update paymentOrder status
        paymentOrderRepository.updatePaymentOrderStatusByOrderId(command.getOrderId(), command.getStatus());

        String pspRawData = "";
        try {
            pspRawData = objectMapper.writeValueAsString(command.getExtraDetails().getPspRawData());
        }catch (Exception e) {
            log.error(e.getMessage());
        }



        //paymentEvent
        paymentEventRepository.updatePaymentEventExtraDetails(command.getOrderId(),
                command.getExtraDetails().getOrderName(),
                command.getExtraDetails().getMethod(),
                command.getExtraDetails().getApprovedAt(),
                command.getExtraDetails().getType(),
                pspRawData);
        return true;
        
    }


    public void insertPaymentHistory(List<PaymentOrder> orders, PaymentOrderStatus status,String reason){
        if(orders.isEmpty()){
            throw new IllegalArgumentException("주문이 존재하지 않습니다.");
        }
        PaymentOrder paymentOrder = orders.get(0);


        PaymentOrderHistories paymentOrderHistories =
                    new PaymentOrderHistories(paymentOrder, paymentOrder.getPaymentOrderStatus(), status, reason);
            paymentOrderHistoriesRepository.saveAndFlush(paymentOrderHistories);

    }

    public static void updatePaymentOrderStatus(List<PaymentOrder> paymentOrders) {
        paymentOrders.stream().forEach(paymentOrder -> {
            if(paymentOrder.getPaymentOrderStatus() == PaymentOrderStatus.NOT_STARTED ||
                    paymentOrder.getPaymentOrderStatus() == PaymentOrderStatus.EXECUTING){
                paymentOrder.setPaymentStatus(PaymentOrderStatus.EXECUTING);
            }else  if(paymentOrder.getPaymentOrderStatus() == PaymentOrderStatus.SUCCESS) {
                throw new PaymentAlreadyException("이미 결제가 완료된 주문입니다.");
            } else if(paymentOrder.getPaymentOrderStatus() == PaymentOrderStatus.FAILURE){
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
