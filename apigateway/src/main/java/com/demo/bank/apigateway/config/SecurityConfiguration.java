package com.demo.bank.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    SecurityWebFilterChain security(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .pathMatchers(HttpMethod.GET, "/auth/me").authenticated()
                        .pathMatchers(HttpMethod.GET, "/v1/accounts/*").authenticated()
                        .pathMatchers(HttpMethod.POST, "/v1/transactions/debit").authenticated()
                        .pathMatchers(HttpMethod.POST, "/v1/transactions/transfers").authenticated()
                        .pathMatchers(HttpMethod.POST, "/v1/transactions/credit")
                        .access((authentication, context) -> authentication
                                .<AuthorizationResult>map(current -> new AuthorizationDecision(
                                        current instanceof JwtAuthenticationToken jwt
                                                && jwt.isAuthenticated()
                                                && "OPERATOR".equals(jwt.getToken().getClaimAsString("role"))
                                ))
                                .defaultIfEmpty(new AuthorizationDecision(false))
                        )
                        .anyExchange().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        return http.build();
    }
}
