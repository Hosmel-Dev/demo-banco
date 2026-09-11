package com.demo.bank.transaction.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final String accountConnection;

    public WebClientConfig(
            @Value("${web.client.account}") String accountConnection){
        this.accountConnection = accountConnection;
    }

    @Bean
    public WebClient accountWebClient(){
        return WebClient.builder()
                .baseUrl(accountConnection)
                .build();
    }
}
