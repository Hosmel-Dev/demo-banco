package com.demo.bank.transaction.infrastructure.adapter.out.persistance.http.account;

import com.demo.bank.transaction.domain.enums.AppCurrency;
import com.demo.bank.transaction.domain.model.Money;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Data
@Getter
@Setter
@Builder
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private Money balance;
    private LocalDateTime createdAt;
}
