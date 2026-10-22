package com.demo.bank.account.infrastructure.adapter.in.rest.mapper;

import com.demo.bank.account.application.dto.result.ValidateIdentityResult;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.response.ValidateIdentityResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IdentityRestMapper {
    ValidateIdentityResponse toResponse(ValidateIdentityResult result);
}
