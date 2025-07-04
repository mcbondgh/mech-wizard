package com.mech.app.dataproviders.transactions;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class TransactionsDataProvider {
    private int customerId, recordId;
    private String transactionId;
    private String paymentMethod;
    private String notes;
    private double amount;
    private int userId;
    private String transactionType;
    private Timestamp transactionDate;

    public record TransactionRecord(int jobId, String customerName, String serviceType, Date serviceDate, double account_balance, double serviceCost, double itemsCost, String status){

        public String jobNo() {
            int month = serviceDate.toLocalDate().getMonthValue();
            return String.format("JC/%d/%d", month, jobId);
        }
        public String formattedDate() {
            return serviceDate.toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
        }

    }


    public TransactionsDataProvider() {

    }

    public TransactionsDataProvider(int customerId, String transactionId, String transactionType, String paymentMethod, String notes, int userId, Timestamp transactionDate, double amount) {
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        this.userId = userId;
        this.transactionDate = transactionDate;
        this.amount = amount;
    }

    //SETTERS AND GETTERS


    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }
}//END OF CLASS...
