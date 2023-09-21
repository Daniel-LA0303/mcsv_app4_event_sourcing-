package com.bankelectronik.commands.aggregates;

import com.bankelectronik.commonapi.commands.CreateAccountCommand;
import com.bankelectronik.commonapi.commands.CreditAccountCommand;
import com.bankelectronik.commonapi.commands.DebitAccountCommand;
import com.bankelectronik.commonapi.enums.AccountStatus;
import com.bankelectronik.commonapi.events.AccountCreateEvent;
import com.bankelectronik.commonapi.events.AccountCreditEvent;
import com.bankelectronik.commonapi.events.AccountDebitEvent;
import com.bankelectronik.commonapi.exceptions.NegativeInitialBalanceException;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@Slf4j
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;

    private String currency;

    private double balance;

    private AccountStatus accountStatus;

    public AccountAggregate(){

    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand command){
        log.info("AccountAggregate CreateAccountCommand");
        if(command.getInitialBalance() < 0){
            throw new NegativeInitialBalanceException("Initial balance cannot be negative");
        }
        AggregateLifecycle.apply(new AccountCreateEvent(command.getId(), command.getCurrency(), command.getInitialBalance(), AccountStatus.CREATED));
    }

    @EventSourcingHandler
    public void on(AccountCreateEvent event){
        log.info("AccountCreateEvent received");
        this.accountId = event.getId();
        this.currency = event.getCurrency();
        this.balance = event.getBalance();
        this.accountStatus = event.getStatus();
    }

    @CommandHandler
    public void handle(CreditAccountCommand command){
        log.info("AccountAggregate CreditAccountCommand");
        if(command.getAmount() < 0){
            throw new NegativeInitialBalanceException("Credit amount cannot be negative");
        }
        AggregateLifecycle.apply(new AccountCreditEvent(command.getId(), command.getCurrency(), command.getAmount()));
    }

    @EventSourcingHandler
    public void on(AccountCreditEvent event){
        log.info("AccountCreditEvent received");
        this.balance += event.getAmount();
    }

    @CommandHandler
    public void handle(DebitAccountCommand command){
        log.info("AccountAggregate DebitAccountCommand");
        if(command.getAmount() < 0){
            throw new NegativeInitialBalanceException("Debit amount cannot be negative");
        }
        if(command.getAmount() > this.balance){
            throw new NegativeInitialBalanceException("Debit amount cannot be greater than balance");
        }
        AggregateLifecycle.apply(new AccountDebitEvent(command.getId(), command.getCurrency(), command.getAmount()));
    }

    @EventSourcingHandler
    public void on(AccountDebitEvent event){
        log.info("AccountDebitEvent received");
        this.balance -= event.getAmount();
    }

}
