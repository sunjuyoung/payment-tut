package com.example.payment.adapter.web.service;

import com.example.payment.adapter.web.config.TossWebClientConfiguration;
import com.example.payment.adapter.web.request.TossPaymentConfirmRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final WebClient tossWebClient;


    private static String uri = "/v1/payments/confirm";



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
