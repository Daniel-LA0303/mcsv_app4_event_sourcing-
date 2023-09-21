package com.bankelectronik.query.dto;

import com.bankelectronik.commonapi.enums.TransactionTYpe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountWatchEvent {

    private Instant instant;

    private String accountId;

    private double currentBalance;

    private TransactionTYpe type;

    private double transactionAmount;
}
