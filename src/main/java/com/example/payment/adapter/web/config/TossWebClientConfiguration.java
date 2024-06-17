package com.example.payment.adapter.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

@Configuration
public class TossWebClientConfiguration {

    @Value("${PSP.toss.secretKey}")
    private String secretKey;
    @Value("${PSP.toss.url}")
    private String baseUrl;

    //인증키를 요청 헤더에 추가하는 WebClient 빈을 생성
    @Bean
    public WebClient tossWebClient() throws UnsupportedEncodingException {
        //시크릿키를 Base64로 인코딩해서 Authorization 헤더에 추가
        Base64.Encoder encoder = Base64.getEncoder();
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes("UTF-8"));
        String encodedSecretKey = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, encodedSecretKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .clientConnector(reactorClientHttpConnector())
                .codecs(configurer -> configurer.defaultCodecs())
                .build();


        return webClient;
    }

    private ClientHttpConnector reactorClientHttpConnector() {
        ConnectionProvider provider = ConnectionProvider.builder("toss-payment")
                .build();
        return new ReactorClientHttpConnector(HttpClient.create(provider));
    }
}
