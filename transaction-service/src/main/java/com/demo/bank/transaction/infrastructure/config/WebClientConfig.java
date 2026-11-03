package com.demo.bank.transaction.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.channel.ChannelOption;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    private final String accountConnection;

    public WebClientConfig(
            @Value("${web.client.account}") String accountConnection){
        this.accountConnection = accountConnection;
    }

    @Bean
    public WebClient accountWebClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .responseTimeout(Duration.ofSeconds(3));

        return builder
                .baseUrl(accountConnection)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(new ServerBearerExchangeFilterFunction())
                .build();
    }
}
