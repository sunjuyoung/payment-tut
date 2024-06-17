package com.example.payment.adapter.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/v1/toss")
public class PaymentController {

    @GetMapping("/success")
    public Mono<String> successPage(){
        return Mono.just("success");
    }

    @GetMapping("/fail")
    public Mono<String> failPage(){
        return Mono.just("fail");
    }
}
