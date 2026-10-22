package com.demo.bank.account.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Configuration
public class JwtConfiguration {

    @Bean
    JwtEncoder jwtEncoder(
            @Value("${bank.jwt.public-key}") String publicPath,
            @Value("${bank.jwt.private-key}") String privatePath
    ) throws IOException {
        try (var publicStream = Files.newInputStream(Path.of(publicPath));
             var privateStream = Files.newInputStream(Path.of(privatePath))) {
            var publicKey = RsaKeyConverters.x509().convert(publicStream);
            var privateKey = RsaKeyConverters.pkcs8().convert(privateStream);
            return NimbusJwtEncoder.withKeyPair(publicKey, privateKey).build();
        }
    }

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
