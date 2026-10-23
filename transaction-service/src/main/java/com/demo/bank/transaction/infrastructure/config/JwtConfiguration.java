package com.demo.bank.transaction.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class JwtConfiguration {

    @Bean
    ReactiveJwtDecoder jwtDecoder(
            @Value("${bank.jwt.public-key}") String publicPath
    ) throws IOException {
        try (var stream = Files.newInputStream(Path.of(publicPath))) {
            var publicKey = RsaKeyConverters.x509().convert(stream);
            var decoder = NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
            decoder.setJwtValidator(
                    JwtValidators.createDefaultWithIssuer("account-service")
            );
            return decoder;
        }
    }
}
