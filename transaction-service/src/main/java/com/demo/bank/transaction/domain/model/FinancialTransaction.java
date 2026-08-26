package com.demo.bank.transaction.domain.model;

import com.demo.bank.transaction.domain.enums.TransactionStatus;
import com.demo.bank.transaction.domain.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FinancialTransaction {
    private Long id;
    private String idempotencyKey;
    private TransactionType type;
    private Money money;
    private TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;

    public void markAsSuccessful(){
        if (status != TransactionStatus.PENDING){
            System.out.println();
        }
        this.status = TransactionStatus.SUCCESS;
        this.createdAt = LocalDateTime.now();
    }

}
