package com.aflr.spring_batch.repositories;

import com.aflr.spring_batch.domains.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
}
