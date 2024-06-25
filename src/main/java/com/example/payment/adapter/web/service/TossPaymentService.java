package com.example.payment.adapter.web.service;

import com.example.payment.adapter.web.config.TossWebClientConfiguration;
import com.example.payment.adapter.web.domain.enums.PSPConfirmStatus;
import com.example.payment.adapter.web.domain.enums.PaymentMethod;
import com.example.payment.adapter.web.domain.enums.PaymentOrderStatus;
import com.example.payment.adapter.web.domain.enums.PaymentType;
import com.example.payment.adapter.web.request.TossPaymentConfirmRequest;
import com.example.payment.adapter.web.response.PaymentExecutionResult;
import com.example.payment.adapter.web.response.TossPaymentConfirmResponse;
import com.example.payment.adapter.web.service.in.Failure;
import com.example.payment.adapter.web.service.in.PaymentConfirmCommand;
import com.example.payment.adapter.web.service.in.PaymentExtraDetails;
import com.example.payment.common.exception.PSPConfirmationException;
import com.example.payment.common.exception.TossPaymentError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final WebClient tossWebClient;


    private static String uri = "/v1/payments/confirm";

    // 결제 승인 API를 호출
    public Mono<PaymentExecutionResult> execute(PaymentConfirmCommand command){
        log.info("----------TossPaymentService.execute====================");


       return tossWebClient.post()
                .uri(uri)
                .header("Idempotency-Key", command.getOrderId())
                .bodyValue(
                        """
{
                            "paymentKey": "%s",
                            "orderId": "%s",
                            "amount": "%s"
                        }
                        """.formatted(command.getPaymentKey(), command.getOrderId(), command.getAmount())

                )
                .retrieve()
               .onStatus(statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(), response ->
                       response.bodyToMono(Failure.class)
                               .flatMap(failure -> {
                                   TossPaymentError error = TossPaymentError.get(failure.getCode());
                                      return Mono.error(new PSPConfirmationException(error.name(), error.getMessage(),error.isRetryableError()));
                               })
               )
                .bodyToMono(TossPaymentConfirmResponse.class)
                .map(res->{
                    PaymentExtraDetails extraDetails = PaymentExtraDetails.builder()
                            .type(PaymentType.valueOf(res.getType()))
                            .method(PaymentMethod.getPaymentMethod(res.getMethod()))
                            .approvedAt(LocalDateTime.parse(res.getApprovedAt(), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            .orderName(res.getOrderName())
                            .pspConfirmStatus(PSPConfirmStatus.valueOf(res.getStatus()))
                            .totalAmount((long) res.getTotalAmount())
                            .pspRawData(res.toString())
                            .build();
                   return PaymentExecutionResult.builder()
                            .extraDetails(extraDetails)
                            .paymentKey(command.getPaymentKey())
                            .orderId(command.getOrderId())
                            .isSuccess(true)
                            .build();
                })

                ;

    }


    // 결제 승인 API를 호출
    // 결제를 승인하면 결제수단에서 금액이 차감돼요.
    public Mono<String> executePaymentConfirm(String paymentKey, String orderId, String amount) {

        return tossWebClient.post()
                .uri(uri)
                .bodyValue(
                        """
{
                            "paymentKey": "%s",
                            "orderId": "%s",
                            "amount": "%s"
                        }
                        """.formatted(paymentKey, orderId, amount)

                )
                .retrieve()
                .bodyToMono(String.class)
                ;
    }
}
