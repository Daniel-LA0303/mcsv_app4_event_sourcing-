package com.bankelectronik.commonapi.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditAccountRequestDTO {

    private String accountId;
    private double currency;
    private double amount;
}
