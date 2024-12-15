package com.aflr.spring_batch.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private Integer id;
    private String transactionIdSource;
    private String clientId;
    private Double amount;
    private ZonedDateTime transactionDate;
    private String rawTransaction;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRawTransaction() {
        return rawTransaction;
    }

    public void setRawTransaction(String rawTransaction) {
        this.rawTransaction = rawTransaction;
    }

    public ZonedDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(ZonedDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionIdSource() {
        return transactionIdSource;
    }

    public void setTransactionIdSource(String transactionIdSource) {
        this.transactionIdSource = transactionIdSource;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", id=" + id +
                ", transactionIdSource='" + transactionIdSource + '\'' +
                ", clientId='" + clientId + '\'' +
                ", transactionDate=" + transactionDate +
                ", rawTransaction='" + rawTransaction + '\'' +
                '}';
    }
}
