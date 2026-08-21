package com.demo.bank.transaction.domain.model;

import com.demo.bank.transaction.domain.enums.Direction;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LedgerEntry {
    private Long id;
    private Long transactionId;
    private Long accountId;
    private Direction direction;
    private Money amount;

}
