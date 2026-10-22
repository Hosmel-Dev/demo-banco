package com.demo.bank.account.infrastructure.adapter.in.rest;

import com.demo.bank.account.application.dto.command.ValidateIdentityCommand;
import com.demo.bank.account.application.port.in.ValidateIdentityUseCase;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.request.ValidateIdentityRequest;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.response.ValidateIdentityResponse;
import com.demo.bank.account.infrastructure.adapter.in.rest.mapper.IdentityRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/login")
public class IdentityController {
    private final ValidateIdentityUseCase validateIdentityUseCase;
    private final IdentityRestMapper mapper;
    @PostMapping
    public Mono<ValidateIdentityResponse> validateIdentity(@Valid @RequestBody ValidateIdentityRequest request){
        var command = new ValidateIdentityCommand(request.username(),request.password());
        return validateIdentityUseCase.execute(command).map(mapper::toResponse);
    }
}
