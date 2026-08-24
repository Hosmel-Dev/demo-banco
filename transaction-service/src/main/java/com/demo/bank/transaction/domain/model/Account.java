package com.demo.bank.transaction.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class Account {
    private Long id;
    private Money money;
}
