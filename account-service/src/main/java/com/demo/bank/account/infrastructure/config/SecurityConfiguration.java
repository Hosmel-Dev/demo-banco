package com.demo.bank.account.infrastructure.config;

import java.time.Duration;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    private static final String[] NO_AUTH_LIST = {
            "/auth/login"
    };

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {


        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.POST, NO_AUTH_LIST).permitAll()
                        .pathMatchers(HttpMethod.POST, "/v1/accounts","/v1/accounts/**").authenticated()
                        .pathMatchers(HttpMethod.GET,  "/v1/accounts","/v1/accounts/**").authenticated()
                        .anyExchange().authenticated()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);
        return http.build();


    }

}
