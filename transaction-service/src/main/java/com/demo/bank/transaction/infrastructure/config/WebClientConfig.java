package com.demo.bank.transaction.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient accountWebClient(){
        return WebClient.builder()
                .baseUrl("http://account-service:8100/v1")
                .build();
    }
}
