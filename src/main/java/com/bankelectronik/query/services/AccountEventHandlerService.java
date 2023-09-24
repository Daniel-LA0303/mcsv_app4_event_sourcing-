package com.bankelectronik.query.services;


import com.bankelectronik.commonapi.enums.TransactionTYpe;
import com.bankelectronik.commonapi.events.AccountCreateEvent;
import com.bankelectronik.commonapi.events.AccountCreditEvent;
import com.bankelectronik.commonapi.events.AccountDebitEvent;
import com.bankelectronik.query.dto.AccountWatchEvent;
import com.bankelectronik.query.entities.Account;
import com.bankelectronik.query.entities.AccountTransaction;
import com.bankelectronik.query.queries.GetAccountBalanceStream;
import com.bankelectronik.query.queries.GetAccountById;
import com.bankelectronik.query.queries.GetAllAccounts;
import com.bankelectronik.query.repository.AccountRepository;
import com.bankelectronik.query.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class AccountEventHandlerService {
    //this class manages the event handlers for the account and transaction events


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;


    //save action
    @EventHandler
    public void on(AccountCreateEvent accountCreateEvent, EventMessage<AccountCreateEvent> eventMessage) {
        log.info("*************************");
        log.info("AccountCreateEvent received");

        Account account = new Account();
        account.setId(accountCreateEvent.getId());
        account.setBalance(accountCreateEvent.getBalance());
        account.setStatus(accountCreateEvent.getStatus());
        account.setCurrency(accountCreateEvent.getCurrency());
        account.setCreatedAT(eventMessage.getTimestamp());

        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountCreditEvent accountCreditEvent, EventMessage<AccountCreateEvent> eventMessage){
        log.info("*************************");
        log.info("AccountCreateEvent received");

        Account account = accountRepository.findById(accountCreditEvent.getId()).get();
        AccountTransaction accountTransaction = AccountTransaction.builder()
                        .account(account)
                        .amount(accountCreditEvent.getAmount())
                        .transactionTYpe(TransactionTYpe.CREDIT)
                        .timestamp(eventMessage.getTimestamp())
                        .build();

        transactionRepository.save(accountTransaction);
        account.setBalance(account.getBalance() + accountCreditEvent.getAmount());
        accountRepository.save(account);

        AccountWatchEvent accountWatchEvent = new AccountWatchEvent(
                accountTransaction.getTimestamp(),
                account.getId(),
                account.getBalance(),
                accountTransaction.getTransactionTYpe(),
                accountTransaction.getAmount()
        );
        queryUpdateEmitter.emit(GetAccountBalanceStream.class, (query) -> (query.getAccountId().equals(account.getId())), accountWatchEvent);
    }

    @EventHandler
    public void on(AccountDebitEvent accountDeditEvent, EventMessage<AccountCreateEvent> eventMessage){
        log.info("*************************");
        log.info("AccountCreateEvent received");

        Account account = accountRepository.findById(accountDeditEvent.getId()).get();
        AccountTransaction accountTransaction = AccountTransaction.builder()
                .account(account)
                .amount(accountDeditEvent.getAmount())
                .transactionTYpe(TransactionTYpe.DEBIT)
                .timestamp(eventMessage.getTimestamp())
                .build();

        transactionRepository.save(accountTransaction);
        account.setBalance(account.getBalance() - accountDeditEvent.getAmount());
        accountRepository.save(account);

        AccountWatchEvent accountWatchEvent = new AccountWatchEvent(
                accountTransaction.getTimestamp(),
                account.getId(),
                account.getBalance(),
                accountTransaction.getTransactionTYpe(),
                accountTransaction.getAmount()
        );
        queryUpdateEmitter.emit(GetAccountBalanceStream.class, (query) -> (query.getAccountId().equals(account.getId())), accountWatchEvent);
    }

    ///Query handlers
    @QueryHandler
    public List<Account> on(GetAllAccounts query){
       return accountRepository.findAll();
    }

    @QueryHandler
    public Account on(GetAccountById query){
        return accountRepository.findById(query.getAccountId()).get();
    }

    @QueryHandler
    public AccountWatchEvent on(GetAccountBalanceStream query){
        Account account = accountRepository.findById(query.getAccountId()).get();
        AccountTransaction accountTransaction = transactionRepository.findTop1ByAccountIdOrderByTimestampDesc(query.getAccountId());

        if(accountTransaction != null){
            return new AccountWatchEvent(
                    accountTransaction.getTimestamp(),
                    account.getId(),
                    account.getBalance(),
                    accountTransaction.getTransactionTYpe(),
                    accountTransaction.getAmount()
            );
        }

        return null; //if no transaction has been made yet
    }

}
