package com.aflr.spring_batch.dtos;

import java.time.ZonedDateTime;

public class TransactionDto {

    private String transactionIdSource;
    private String clientId;
    private double amount;
    private ZonedDateTime transactionDate;

    public String getTransactionIdSource() {
        return transactionIdSource;
    }

    public void setTransactionIdSource(String transactionIdSource) {
        this.transactionIdSource = transactionIdSource;
    }

    public ZonedDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(ZonedDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
