package com.demo.bank.account.infrastructure.adapter.in.rest;

import com.demo.bank.account.application.dto.command.ValidateIdentityCommand;
import com.demo.bank.account.application.port.in.ValidateIdentityUseCase;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.request.ValidateIdentityRequest;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.response.ValidateIdentityResponse;
import com.demo.bank.account.infrastructure.adapter.in.rest.mapper.IdentityRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class IdentityController {
    private final ValidateIdentityUseCase validateIdentityUseCase;
    private final IdentityRestMapper mapper;

    @PostMapping("/login")
    public Mono<ValidateIdentityResponse> validateIdentity(@Valid @RequestBody ValidateIdentityRequest request){
        var command = new ValidateIdentityCommand(request.username(),request.password());
        return validateIdentityUseCase.execute(command).map(mapper::toResponse);
    }

    @GetMapping("/me")
    public Mono<Map<String, String>> me(@AuthenticationPrincipal Jwt jwt) {
        return Mono.just(Map.of("userId", jwt.getSubject()));
    }
}
