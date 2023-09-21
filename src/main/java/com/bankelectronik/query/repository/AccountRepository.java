package com.bankelectronik.query.repository;

import com.bankelectronik.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String>{


}
