package com.demo.bank.transaction.infrastructure.adapter.out.persistance.http.account;

import com.demo.bank.transaction.domain.enums.AppCurrency;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@Builder
public class AccountRequest {
    private Long id;
    private BigDecimal amount;
    private AppCurrency currency;
}
