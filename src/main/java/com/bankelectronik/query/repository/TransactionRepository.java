package com.bankelectronik.query.repository;

import com.bankelectronik.query.entities.AccountTransaction;
import org.hibernate.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<AccountTransaction, Long> {

    AccountTransaction findTop1ByAccountIdOrderByTimestampDesc(String accountId);
}
