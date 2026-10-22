package com.demo.bank.account.infrastructure.adapter.out.persistance;

import com.demo.bank.account.application.port.out.TokenIssuePort;
import com.demo.bank.account.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
@Component
@RequiredArgsConstructor
public class JwtTokenIssueAdapter implements TokenIssuePort {
    private final JwtEncoder encoder;

    @Override
    public Mono<String> issue(Long userId, String username, UserRole role) {
        return Mono.fromCallable(()-> {
            Instant now = Instant.now();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("account-service")
                    .subject(userId.toString())
                    .issuedAt(now)
                    .expiresAt(now.plus(Duration.ofMinutes(15)))
                    .claim("username", username)
                    .claim("role", role.name())
                    .build();

            return encoder.encode(JwtEncoderParameters.from(claims))
                    .getTokenValue();
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
