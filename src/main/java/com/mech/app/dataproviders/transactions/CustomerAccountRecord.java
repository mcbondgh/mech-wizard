package com.mech.app.dataproviders.transactions;

import java.sql.Timestamp;

public class CustomerAccountRecord {
    private int recordId;
    private int customerId;
    private double amount;
    private Timestamp transactionDate;
    private int userId;

    public record nameAndAccountData(String name, String mobileNumber, int customerId, double amount) {}

    public CustomerAccountRecord() {}

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}//end of class...

