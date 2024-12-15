package com.aflr.spring_batch.config;

import com.aflr.spring_batch.domains.Transaction;
import com.aflr.spring_batch.dtos.TransactionDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

public class TransactionProcessor implements ItemProcessor<TransactionDto, Transaction> {
    @Override
    public Transaction process(TransactionDto item) {
        Transaction txn = new Transaction();
        BeanUtils.copyProperties(item, txn);
        txn.setRawTransaction(item.toString());
        return txn;
    }
}
