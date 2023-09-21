package com.bankelectronik.commonapi.events;

import com.bankelectronik.commonapi.enums.AccountStatus;
import lombok.Getter;

public class AccountCreateEvent extends BaseEvent<String>{

    @Getter
    private String currency;

    @Getter
    private double balance;

    @Getter
    private AccountStatus status;

    public AccountCreateEvent(String id, String currency, double balance, AccountStatus status){
        super(id);
        this.currency = currency;
        this.balance = balance;
        this.status = status;
    }
}
