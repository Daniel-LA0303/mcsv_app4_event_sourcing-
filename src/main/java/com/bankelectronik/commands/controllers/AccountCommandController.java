package com.bankelectronik.commands.controllers;

import com.bankelectronik.commonapi.commands.CreateAccountCommand;
import com.bankelectronik.commonapi.commands.CreditAccountCommand;
import com.bankelectronik.commonapi.commands.DebitAccountCommand;
import com.bankelectronik.commonapi.dtos.CreateAccountRequestDTO;
import com.bankelectronik.commonapi.dtos.CreditAccountRequestDTO;
import com.bankelectronik.commonapi.dtos.DebitAccountRequestDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/commands/accounts")
public class AccountCommandController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping("/create")
    public CompletableFuture<String> createAccount(@RequestBody CreateAccountRequestDTO createAccountRequestDTO){
        return commandGateway.send(new CreateAccountCommand(
                UUID.randomUUID().toString(),
                createAccountRequestDTO.getCurrency(),
                createAccountRequestDTO.getInitialBalance()
        ));
    }

    @PostMapping("/debit")
    public CompletableFuture<String> debitAccount(@RequestBody DebitAccountRequestDTO debitAccountDTO){
        return commandGateway.send(new DebitAccountCommand(
                debitAccountDTO.getAccountId(),
                debitAccountDTO.getCurrency(),
                debitAccountDTO.getAmount()
        ));
    }

    @PostMapping("/credit")
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDTO creditAccountDTO){
        return commandGateway.send(new CreditAccountCommand(
                creditAccountDTO.getAccountId(),
                creditAccountDTO.getCurrency(),
                creditAccountDTO.getAmount()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
