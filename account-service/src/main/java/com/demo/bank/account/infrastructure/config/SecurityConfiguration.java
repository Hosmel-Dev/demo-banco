package com.demo.bank.account.infrastructure.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
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

    private static final String INTERNAL_HEADER = "X-Internal-Service-Key";
    private final byte[] internalDebitKey;

    public SecurityConfiguration(
            @Value("${bank.internal.debit.key}") String internalDebitKey) {
        if (internalDebitKey.isBlank()) {
            throw new IllegalArgumentException("Falta la clave interna de débito");
        }
        this.internalDebitKey = internalDebitKey.getBytes(StandardCharsets.UTF_8);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .pathMatchers(HttpMethod.GET, "/auth/me").authenticated()
                        .pathMatchers(HttpMethod.GET, "/v1/accounts/*").authenticated()
                        .pathMatchers(HttpMethod.POST, "/v1/accounts/debit")
                        .access((authentication, context) -> authentication
                                .<AuthorizationResult>map(current -> new AuthorizationDecision(
                                        current instanceof JwtAuthenticationToken
                                                && current.isAuthenticated()
                                                && validInternalKey(
                                                context.getExchange()
                                                        .getRequest()
                                                        .getHeaders()
                                                        .getFirst(INTERNAL_HEADER)
                                        )
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

    private boolean validInternalKey(String received) {
        return received != null
                && MessageDigest.isEqual(
                internalDebitKey,
                received.getBytes(StandardCharsets.UTF_8)
        );
    }
}