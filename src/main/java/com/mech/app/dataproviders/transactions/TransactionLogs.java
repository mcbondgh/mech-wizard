package com.mech.app.dataproviders.transactions;

import java.sql.Timestamp;

public class TransactionLogs {
    private String transactionId;
    private String transactionType;
    private String paymentMethod;
    private String notes;
    private int userId, recordId;
    private Timestamp transactionDate;
    private double amount;

    public TransactionLogs() {
        // Default constructor
    }
    public TransactionLogs(String transactionId, String transactionType, String paymentMethod, String notes, int userId, int recordId, Timestamp transactionDate, double amount) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        this.userId = userId;
        this.recordId = recordId;
        this.transactionDate = transactionDate;
        this.amount = amount;
    }

    //SETTERS AND GETTERS SECTION

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}//end of class...
